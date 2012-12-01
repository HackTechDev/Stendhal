<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/xml.php');
require_once($IP.'/../scripts/items.php');
require_once($IP.'/../scripts/urlrewrite.php');


/**
 * helper function to read the item with this name using the Stendhal website code
 */
function getItemByName($name) {
	$name = strtolower($name);
	$items = getItems();

	foreach($items as $item) {
		if($item->name==$name) {
			return $item;
		}
	}
	return NULL;
}


/**
 * reads the icon of a Stendhal items
 */
function stendhalDataIncludeItemIconOnly($item) {
	$res = '<span class="stendhalItemIcon"><a href="'.rewriteURL('/item/'.surlencode($item->class).'/'.surlencode($item->name).'.html').'">';
	$res .= '<img src="' . htmlspecialchars($item->gfx) . '" />';
	$res .= '</a></span>';
	return $res;
}

/**
 * reads stats and optionally the icon and the description of Stendhal items
 */
function stendhalDataIncludeItemStats($item, $argv) {
	$res = '<div class="stendhalItem"><span class="stendhalItemIconNameBanner">';

	if (!isset($argv['info'])) {
		$res .= stendhalDataIncludeItemIconOnly($item);
	}

	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<a href="'.rewriteURL('/item/'.surlencode($item->class).'/'.surlencode($item->name).'.html').'">';
		$res .= $item->name;
		$res .= '</a>';
	}
	$res .= '</span>';
		
	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<br />';
		$res .= 'Class: ' . htmlspecialchars(ucfirst($item->class)) . '<br />';
		foreach($item->attributes as $label=>$data) {
			if ($label != "quantity") {
				$res .= htmlspecialchars(ucfirst($label)) . ': ' . htmlspecialchars($data) . '<br />';
			}
		}
	}
	if (!isset($argv['info'])) {
		$res .= '<br />' . $item->description . '<br />';
	}

	$res .= '</div>';
	return $res;
}

/**
 * includes data about Stendhal items
 */
function stendhalDataIncludeItem($input, $argv, $parser) {
	$parsedInput = stendhalDataIncludeParseInput($input);

	$res = '';
	$item = getItemByName($parsedInput['name']);
	if ($item == NULL) {
		return '&lt;item "' . htmlspecialchars($parsedInput['name']) . '" not found&gt;';
	}

	if (isset($argv['info']) && ($argv['info'] == 'icon')) {
		$res .= stendhalDataIncludeItemIconOnly($item);
	} else {
		$res .= stendhalDataIncludeItemStats($item, $argv);
	}

	$link = rewriteURL('/item/' . surlencode($item->class) .'/'. surlencode($item->name) .'.html');
	$res = stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $parsedInput['display'], "stendhalItemLink", $res);

	return $res;
}

?>
