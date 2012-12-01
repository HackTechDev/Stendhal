<?php
/*
 Stendhal website - a website to manage and ease playing of Stendhal game
 Copyright (C) 2008-2011 Stendhal
 Copyright (C) 2008  Miguel Angel Blanch Lardin

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

$OUTFITS_BASE="data/sprites/outfit";

require_once 'configuration.php';

// Imagick takes 'mixed' for colors, but ints didn't work. This seems to.
function color_name($color) {
	// Drop alpha
	$name = dechex($color & 0xffffff);
	while (strlen($name) < 6) {
		$name = '0' . $name;
	}
	return '#' . $name;
}

/*
 * Color an image roughly like Stendhal Blend.TrueColor does.
 * 
 * params:
 * Imagick image
 * int color
 */
function color_image($image, $color) {
	// Ensure the target image does not have 0 saturation. First grayscale
	// it, and then recolour it with known saturation.
	$image->modulateImage(100, 0, 100); // grayscale
	$overlay = new Imagick();	
	$overlay->newImage($image->getImageWidth(),
		$image->getImageHeight(), 'red', 'png');
	$clone = $image->clone();
	// keep alpha
	$clone->compositeImage($overlay, imagick::COMPOSITE_SRCIN, 0, 0);
	$image->compositeImage($clone, imagick::COMPOSITE_OVERLAY, 0, 0);
	$clone->destroy();

	// color layer
	$overlay->newImage($image->getImageWidth(),
		$image->getImageHeight(), color_name($color), 'png');
	// Color mask of the outfit part. Would not be needed if
	// colorize blend didn't handle alpha in an incompatible way.
	$clone = $image->clone();
	$clone->compositeImage($overlay, imagick::COMPOSITE_SRCIN, 0, 0);
	$overlay->destroy();

	// this is otherwise the usual hue blend, except that it
	// overwrites alpha, sigh
	$image->compositeImage($clone, imagick::COMPOSITE_HUE, 0, 0);
	$clone->destroy();

	// Imagick saturation filter is broken for low saturations. 
	// Calculate adjustment.
	$r = (($color >> 16) & 0xff) / 255.0;
	$g = (($color >> 8) & 0xff) / 255.0;
	$b = ($color & 0xff) / 255.0;
	$max_color = max($r, $g, $b);
	$min_color = min($r, $g, $b);
	$lightness = ($max_color + $min_color) / 2;
	$diff = $max_color - $min_color;
	if ($diff < 0.001) {
		$saturation = 0;
	} else {
		if ($lightness < 0.5) {
			$saturation = $diff / ($max_color + $min_color);
		} else {
			$saturation = $diff / (2 - $max_color - $min_color);
		}
	}
	// Red colored image (like the adjusted base image) has saturation 1;
	// adjust it according to the saturation of our painting color.
	$adj_sat = 100 * $saturation;
	// Adjusting brightness does not work exactly as TrueColor does it.
	// TrueColor does a parabolic bend in the lightness curve; Imagick does
	// some other nonlinear adjustment. Hopefully this is close enough.
	$adj_bright = 50 + 100 * $lightness;
	$image->modulateImage($adj_bright, $adj_sat, 100); // LSH
}

/*
 * Load a part of an outfit
 */
function load_part($part_name, $index, $offset) {
	global $OUTFITS_BASE;
	$location = $OUTFITS_BASE . $part_name . $index . '.png';
	// A workaround for imagick crashing when the file does not
	// exist.
	if (file_exists($location)) {
		$image = new Imagick($location);
		$image->cropImage(48, 64, 0, $offset * 64);
		return $image;
	}
	return 0;
}

/*
 * Paint a colored image over outfit
 */
function composite_with_color($outfit, $overlay, $color) {
	if ($overlay) {
		if ($color) {
			color_image($overlay, $color);
		}
		$outfit->compositeImage($overlay, imagick::COMPOSITE_OVER, 0, 0);
	}
}

/*
 * Create an outfit image.
 */
function create_outfit($completeOutfit, $offset) {
	// outfit code
	$code = $completeOutfit[0];
	// The client won't let select pure black, so 0 works for no color.
	$detailColor = 0;
	$hairColor = 0;
	$dressColor = 0;
	if (count($completeOutfit) > 1) {
		$detailColor = hexdec($completeOutfit[1]);
		$hairColor = hexdec($completeOutfit[2]);
		$dressColor = hexdec($completeOutfit[4]);
	}

	// body:
	$index = $code % 100;
	$bodyIndex = $index;
	$outfit = load_part('/player_base_', $index, $offset);
	if (!$outfit) {
		// ensure we have something to draw on
		$outfit = new Imagick();
		$outfit->newImage(48, 64, 'transparent', 'png');
	}

	// dress
	$code /= 100;		
	$index = $code % 100;
	if (($index == 0) && ($bodyIndex < 50)) {
		$index = 91;
	}
	if ($index) {
		$tmp = load_part('/dress_', $index, $offset);
	} else {
		$tmp = 0;
	}
	composite_with_color($outfit, $tmp, $dressColor);

	// head
	$code /= 100;		
	$index = $code % 100;
	$tmp = load_part('/head_', $index, $offset);
	if ($tmp) {
		$outfit->compositeImage($tmp, imagick::COMPOSITE_OVER, 0, 0);
	}

	// hair
	$code /= 100;		
	$index = $code % 100;
	if ($index) {
		$tmp = load_part('/hair_', $index, $offset);
	} else {
		$tmp = 0;
	}
	composite_with_color($outfit, $tmp, $hairColor);

	// detail
	$code /= 100;		
	$index = $code % 100;
	if ($index) {
		$tmp = load_part('/detail_', $index, $offset);
	} else {
		$tmp = 0;
	}
	composite_with_color($outfit, $tmp, $detailColor);

	return $outfit;
}

/**
 * tries to load an outfit from the file cache, creates and stores it otherwise
 *
 *
 * @param string $completeOutfit the outfit string, needs to be validated before
 * @param int $offset direction, needs to be validated before
 */
function loadOrCreate($completeOutfit, $offset) {
	$cacheIdentifier = '/tmp/outfits/'.$completeOutfit.'-'.$offset.'.png';

	if (file_exists($cacheIdentifier)) {
		readfile($cacheIdentifier);
		return;
	}

	$data = create_outfit(explode('_', $completeOutfit), $offset);

	if (!file_exists('/tmp/outfits')) {
		mkdir('/tmp/outfits', 0755);
	}
	$fp = fopen($cacheIdentifier, 'xb');
	fwrite($fp, $data);
	fclose($fp);
	echo $data;
}


/**
 * validates the input parameter "outfit"
 *
 * @param $outfit
 * @return boolean
 */
function validateInput($outfit) {
	return preg_match('/^[a-f0-9_]+$/', $outfit);
}

$completeOutfit = $_GET['outfit'];
if (isset($_GET['offset'])) {
	$offset = intval($_GET['offset'], 10);
} else {
	$offset = 2;
}

if (!validateInput($completeOutfit)) {
	header('HTTP/1.0 404 Not found');
	exit('Invalid outfit.');
}

$etag = STENDHAL_VERSION.'-'.urlencode($completeOutfit).'-'.$offset;
if (isset($_SERVER['HTTP_IF_NONE_MATCH'])) {
	$requestedEtag = $_SERVER['HTTP_IF_NONE_MATCH'];
}

header("Content-type: image/png");
header("Cache-Control: max-age=3888000, public"); // 45 * 24 * 60 * 60
header('Pragma: cache');
header('Etag: "'.$etag.'"');

if (isset($requestedEtag) && (($requestedEtag == $etag) || ($requestedEtag == '"'.$etag.'"'))) {
	header('HTTP/1.0 304 Not modified');
} else {
	loadOrCreate($completeOutfit, $offset);
}
