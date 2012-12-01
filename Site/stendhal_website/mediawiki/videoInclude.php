<?php
/*
 * videoInclude.php - allows inclusion of videos
 */

# Confirm MW environment
if (defined('MEDIAWIKI')) {

	# Credits
	$wgExtensionCredits['parserhook'][] = array(
		'name'=>'Video Include',
		'author'=>'Hendrik Brummermann',
		'description'=>'Allows inclusion of videos.',
		'version'=>'0.1'
	);

	class VideoInclude {

		function setup( ) {
			global $wgParser, $wgVersion;
			$hook = (version_compare($wgVersion, '1.7', '<')?'#ev':'ev');
			$wgParser->setFunctionHook($hook, array($this, 'parserFunction') );
		}

		function parserFunctionMagic( &$magicWords, $langCode='en' ) {
			$magicWords['ev'] = array( 0, 'ev' );
			return true;
		}

		function parserFunction($parser, $service, $id) {
			if ($service != 'youtube') {
				return "Error: Unknown service";
			}
			$id = htmlspecialchars(urlencode($id));

			return $parser->insertStripItem(
				'<iframe width="420" height="315" src="https://www.youtube-nocookie.com/embed/'.$id.'" frameborder="0" allowfullscreen></iframe>',
					$parser->mStripState);
		}
	}

	# Create global instance and wire it up!
	$wgVideoInclude = new VideoInclude();
	$wgExtensionFunctions[] = array($wgVideoInclude, 'setup');
	$wgHooks['LanguageGetMagic'][] = array($wgVideoInclude, 'parserFunctionMagic');

}

