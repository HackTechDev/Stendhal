<?php
/*
 * stackexchangeInclude.php - allows inclusion of stackexchange stats
 */

# Confirm MW environment
if (defined('MEDIAWIKI')) {

	# Credits
	$wgExtensionCredits['parserhook'][] = array(
		'name'=>'StackExchange Include',
		'author'=>'Hendrik Brummermann',
		'description'=>'Allows inclusion of StackExchange stats.',
		'version'=>'0.1'
	);

	class StackExchangeInclude {

		function setup( ) {
			global $wgParser, $wgVersion;
			$hook = (version_compare($wgVersion, '1.7', '<') ? '#stackexchange':'stackexchange');
			$wgParser->setFunctionHook($hook, array($this, 'parserFunction') );
		}

		function parserFunctionMagic( &$magicWords, $langCode='en' ) {
			$magicWords['stackexchange'] = array( 0, 'stackexchange' );
			return true;
		}

		function parserFunction($parser, $type, $uuid) {
			if ($type == "user") {
				$opts = array(
					'http' => array(
						'method' => 'GET',
						'ignore_errors' => true,
					)
				);
				$url = 'http://stackflair.com/Generate/'.urlencode($uuid).'.html';
				$context = stream_context_create ($opts);
				$html = @file_get_contents($url, false, $context);
				if (isset($html)) {
					return $parser->insertStripItem($html, $parser->mStripState);
				}
				return '';
			} else {
				return 'Unknown first parameter. Please use "user"';
			}
		}

	}

	# Create global instance and wire it up!
	$wgStackExchangeInclude = new StackExchangeInclude();
	$wgExtensionFunctions[] = array($wgStackExchangeInclude, 'setup');
	$wgHooks['LanguageGetMagic'][] = array($wgStackExchangeInclude, 'parserFunctionMagic');

}

