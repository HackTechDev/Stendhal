<?php
/*
 * Ohloh.php - allows inclusion of ohloh stats
 */

# Confirm MW environment
if (defined('MEDIAWIKI')) {

	# Credits
	$wgExtensionCredits['parserhook'][] = array(
		'name'=>'Ohloh Include',
		'author'=>'Hendrik Brummermann',
		'description'=>'Allows inclusion of ohloh stats.',
		'version'=>'0.1'
	);

	class OhlohInclude {

		function setup( ) {
			global $wgParser, $wgVersion;
			$hook = (version_compare($wgVersion, '1.7', '<')?'#ohloh':'ohloh');
			$wgParser->setFunctionHook($hook, array($this, 'parserFunction') );
		}

		function parserFunctionMagic( &$magicWords, $langCode='en' ) {
			$magicWords['ohloh'] = array( 0, 'ohloh' );
			return true;
		}

		function parserFunction($parser, $class, $id, $stat) {
			$class = htmlspecialchars(urlencode($class));
			$id = htmlspecialchars(urlencode($id));
			$stat = htmlspecialchars(urlencode($stat));

			if ($class == "p") {
				return $parser->insertStripItem(
					'<script type="text/javascript" src="http://www.ohloh.net/'.$class.'/'.$id.'/widgets/'.$stat.'.js"></script>',
					$parser->mStripState);
			} else if ($class == "accounts") {
					return $this->parseAccountStats($parser, $id, $stat);
			} else {
				return "Unknown ohloh widget class. Please use one of p, accounts.";
			}
		}

		function parseAccountStats($parser, $id, $stat) {
			$stat = strtolower($stat);
			if ($stat == 'tiny') {
				return $parser->insertStripItem(
					'<a href="http://www.ohloh.net/accounts/'.$id.'?ref=Tiny">'
					.'<img alt="Ohloh profile" height="15" src="http://www.ohloh.net/accounts/'.$id.'/widgets/account_tiny.gif" width="80" />'
					.'</a>');
			} else if ($stat == "rank") {
				return $parser->insertStripItem(
					'<a href="http://www.ohloh.net/accounts/'.$id.'?ref=Rank">'
  					.'<img alt="Ohloh profile" height="24" src="http://www.ohloh.net/accounts/'.$id.'/widgets/account_rank.gif" width="32" />'
					.'</a>');
			} else if ($stat == "detailed") {
				return $parser->insertStripItem(
					'<a href="http://www.ohloh.net/accounts/'.$id.'?ref=Detailed">'
					.'<img alt="Ohloh profile" height="35" src="http://www.ohloh.net/accounts/'.$id.'/widgets/account_detailed.gif" width="191" />'
					.'</a>');
			} else {
				return "Unknown stat. Please use one of tiny, rank, detailed.";
			}
		}
	}

	# Create global instance and wire it up!
	$wgOhlohInclude = new OhlohInclude();
	$wgExtensionFunctions[] = array($wgOhlohInclude, 'setup');
	$wgHooks['LanguageGetMagic'][] = array($wgOhlohInclude, 'parserFunctionMagic');

}

