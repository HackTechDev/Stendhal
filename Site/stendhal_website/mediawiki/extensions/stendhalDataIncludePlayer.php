<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/xml.php');
require_once($IP.'/../scripts/mysql.php');
require_once($IP.'/../scripts/players.php');
require_once($IP.'/../scripts/urlrewrite.php');


/**
 * reads the icon of a Stendhal players
 */
function stendhalDataIncludePlayerIconOnly($player) {
	$res = '<span class="stendhalPlayerIcon"><a href="'.rewriteURL('/character/'.surlencode($player->name).'.html').'">';
	$res .= '<img src="'.rewriteURL('/images/outfit/'.surlencode($player->outfit).'.png').'" />';
	$res .= '</a></span>';
	return $res;
}

function getAge($minutes) {
	return round($minutes/60, 2);
}

/**
 * reads stats and optionally the icon and the description of Stendhal players
 */
function stendhalDataIncludePlayerStats($player, $argv) {
	$res = '<div class="stendhalPlayer"><span class="stendhalPlayerIconNameBanner">';

	if (!isset($argv['info'])) {
		$res .= stendhalDataIncludePlayerIconOnly($player);
	}

	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<a href="'.rewriteURL('/character/'.surlencode($player->name).'.html').'">';
		$res .= $player->name;
		$res .= '</a>';
	}
	$res .= '</span>';
		
	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<br />';
		$res .= 'Age: ' . htmlspecialchars(getAge($player->age)) . '<br />';
		$res .= 'Level: ' . htmlspecialchars($player->level) . '<br />';
		$res .= 'XP: ' . htmlspecialchars($player->xp) . '<br />';
		foreach($player->attributes as $label=>$data) {
			if ($label != 'karma') {
				$res .= htmlspecialchars(ucfirst($label)) . ': ' . htmlspecialchars($data) . '<br />';
			}
		}
		if ($player->adminlevel > 0) {
			if ($player->adminlevel >= 800) {
				$classSuffix = "High";
			} else if ($player->adminlevel >= 400) {
				$classSuffix = "Middle";
			} else {
				$classSuffix = "Low";
			}
			$res .= '<span class="stendhalAdmin stendhalAdmin' . $classSuffix . '">';
			$res .= 'Admin-Level: ' . htmlspecialchars($player->adminlevel) . '</span>' . '<br />';
		}
	}
	if (!isset($argv['info'])) {
		if (strlen($player->sentence) > 0) {
			$res .= '<br />"' . htmlspecialchars($player->sentence) . '"<br />';
		}
	}

	$res .= '</div>';
	return $res;
}

/**
 * includes data about Stendhal players
 */
function stendhalDataIncludePlayer($input, $argv, $parser) {
	$parsedInput = stendhalDataIncludeParseInput($input);

	$parser->disableCache();

	$res = '';
	connect();
	$player = getPlayer($parsedInput['name']);
	disconnect();
	if ($player == NULL) {
		return '&lt;player "' . htmlspecialchars($parsedInput['name']) . '" not found&gt;';
	}

	if (isset($argv['info']) && ($argv['info'] == 'icon')) {
		$res .= stendhalDataIncludePlayerIconOnly($player);
	} else {
		$res .= stendhalDataIncludePlayerStats($player, $argv);
	}

	$link = rewriteURL('/character/' . surlencode($player->name) . '.html');
	$res = stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $parsedInput['display'], "stendhalPlayerLink", $res);

	return $res;
}

?>
