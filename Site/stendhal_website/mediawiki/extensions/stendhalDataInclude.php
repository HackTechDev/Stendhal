<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/xml.php');
require_once($IP.'/../scripts/cache.php');
require_once($IP.'/extensions/stendhalDataIncludeItem.php');
require_once($IP.'/extensions/stendhalDataIncludeCreature.php');
require_once($IP.'/extensions/stendhalDataIncludePlayer.php');
require_once($IP.'/extensions/stendhalDataIncludeNPC.php');

$wgExtensionFunctions[] = "stendhalDataIncludeSetup";
$wgExtensionCredits['other'][] = array(
	'version' => '0.6',
	'name' => 'StendhalDataInclude',
	'author' => 'Hendrik Brummermann',
	'url' => 'http://arianne.sf.net',
	'description' => 'Include Stendhal Data<br>'
		. '&lt;item&gt;rod of the gm&lt;item&gt;<br>'
		. 'info="default" <em>or</em> type="icon" <em>or</em> type="stats"<br>'
		. 'type="default" <em>or</em> type="mouseover" <em>or</em> type="include"<br>'
);




/**
 * wraps the hmtl into a box if the "type" parameter is either undefined
 * or set to "mouseover".
 *
 * @param $argv arguments in the tag
 * @param $link link to include in the normal page
 * @param $name text to show on the link
 * @param $cssclass name of ccs class to render the link
 * @param $html the html code to put into the box
 */
function stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $name, $cssclass, $html) {
	$res = $html;
	if (!isset($argv['type']) || ($argv['type'] == 'mouseover')) {
		$res = '';
		$res .= '<a href="' . $link . '"';
		$res .= ' onmouseover="return overlib(\''.rawurlencode($html).'\', FGCOLOR, \'#000\', BGCOLOR, \'#FFF\',';
		$res .= 'DECODE, FULLHTML';
		$res .= ');" onmouseout="return nd();" class="' . $cssclass . '">';
		$res .= htmlspecialchars($name);
		$res .= '</a>';
	}
	return $res;
}

/**
 * parses the tag content into the name and an optional display test
 */
function stendhalDataIncludeParseInput($input) {
	$temp = explode('|', $input);
	$res  = array();
	$res['name'] = $temp[0];
	$res['display'] = $temp[count($temp) - 1];
	return $res;
}

/**
 * setup the parser by telling it about the tags we can 
 * handle and include the required java script
 */
function stendhalDataIncludeSetup() {
	global $wgParser, $wgScriptPath, $wgOut;
	$wgParser->setHook('item', 'stendhalDataIncludeItem');
	$wgParser->setHook('creature', 'stendhalDataIncludeCreature');
	$wgParser->setHook('player', 'stendhalDataIncludePlayer');
	$wgParser->setHook('npc', 'stendhalDataIncludeNPC');

	$wgOut->addHTML('<script type="text/javascript" src="' . $wgScriptPath . '/extensions/overlibmws/overlibmws.js"></script>');
	$wgOut->addHTML('<script type="text/javascript" src="' . $wgScriptPath . '/extensions/overlibmws/overlibmws_filter.js"></script>');
}

?>
