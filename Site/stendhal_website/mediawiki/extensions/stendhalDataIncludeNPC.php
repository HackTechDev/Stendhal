<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/mysql.php');
require_once($IP.'/../scripts/npcs.php');
require_once($IP.'/../scripts/urlrewrite.php');


/**
 * reads the icon of a Stendhal NPCs
 */
function stendhalDataIncludeNPCIconOnly($NPC) {
	$res = '<span class="stendhalNPCIcon"><a href="'.rewriteURL('/npc/'.surlencode($NPC->name).'.html').'">';
	$res .= '<img src="' . htmlspecialchars($NPC->imagefile) . '" />';
	$res .= '</a></span>';
	return $res;
}

/**
 * reads stats and optionally the icon and the description of Stendhal NPCs
 */
function stendhalDataIncludeNPCStats($NPC, $argv) {
	$res = '<div class="stendhalNPC"><span class="stendhalNPCIconNameBanner">';

	if (!isset($argv['info'])) {
		$res .= stendhalDataIncludeNPCIconOnly($NPC);
	}

	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<a href="'.rewriteURL('/npc/'.surlencode($NPC->name).'.html').'">';
		$res .= $NPC->name;
		$res .= '</a>';
	}
	$res .= '</span>';
		
	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<br />';
		if ($NPC->level > 0) {
			$res .= 'Level: ' . htmlspecialchars($NPC->level) . '<br />';
		}
		$res .= 'HP: ' . htmlspecialchars($NPC->hp) . '/' . htmlspecialchars($NPC->base_hp) . '<br />';
		$res .= $NPC->zone . '<br />';
		if (isset($NPC->pos) && strlen($NPC->pos) > 0) {
			$res .= $NPC->pos . '<br />';
		}
	}
	if (!isset($argv['info'])) {
		if (isset($NPC->description) && strlen($NPC->description) > 0) {
			$res .= '<br />"' . $NPC->description . '"<br />';
		}
	}
	$res .= '</div>';
	return $res;
}

/**
 * includes data about Stendhal NPCs
 */
function stendhalDataIncludeNPC($input, $argv, $parser) {
	$parsedInput = stendhalDataIncludeParseInput($input);

	$parser->disableCache();

	$res = '';
	connect();
	$NPC = NPC::getNPC($parsedInput['name']);
	disconnect();
	if ($NPC == NULL) {
		return '&lt;NPC "' . htmlspecialchars($parsedInput['name']) . '" not found&gt;';
	}

	if (isset($argv['info']) && ($argv['info'] == 'icon')) {
		$res .= stendhalDataIncludeNPCIconOnly($NPC);
	} else {
		$res .= stendhalDataIncludeNPCStats($NPC, $argv);
	}

	$link = rewriteURL('/npc/' . surlencode($NPC->name) . '.html');
	$res = stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $parsedInput['display'], "stendhalNPCLink", $res);

	return $res;
}

?>
