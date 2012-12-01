<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/mysql.php');
require_once($IP.'/../scripts/achievement.php');
require_once($IP.'/../scripts/urlrewrite.php');


/**
 * reads the icon of a Stendhal Achievements
 */
function stendhalDataIncludeAchievementIconOnly($Achievement) {
	$res = '<span class="stendhalAchievementIcon"><a href="'.rewriteURL('/Achievement/'.surlencode($Achievement->name).'.html').'">';
	$res .= '<img src="' . htmlspecialchars($Achievement->imagefile) . '" />';
	$res .= '</a></span>';
	return $res;
}

/**
 * reads stats and optionally the icon and the description of Stendhal Achievements
 */
function stendhalDataIncludeAchievementStats($Achievement, $argv) {
	$res = '<div class="stendhalAchievement"><span class="stendhalAchievementIconNameBanner">';

	if (!isset($argv['info'])) {
		$res .= stendhalDataIncludeAchievementIconOnly($Achievement);
	}

	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<a href="'.rewriteURL('/Achievement/'.surlencode($Achievement->name).'.html').'">';
		$res .= $Achievement->name;
		$res .= '</a>';
	}
	$res .= '</span>';
		
	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<br />';
		if ($Achievement->level > 0) {
			$res .= 'Level: ' . htmlspecialchars($Achievement->level) . '<br />';
		}
		$res .= 'HP: ' . htmlspecialchars($Achievement->hp) . '/' . htmlspecialchars($Achievement->base_hp) . '<br />';
		$res .= $Achievement->zone . '<br />';
		if (isset($Achievement->pos) && strlen($Achievement->pos) > 0) {
			$res .= $Achievement->pos . '<br />';
		}
	}
	if (!isset($argv['info'])) {
		if (isset($Achievement->description) && strlen($Achievement->description) > 0) {
			$res .= '<br />"' . $Achievement->description . '"<br />';
		}
	}
	$res .= '</div>';
	return $res;
}

/**
 * includes data about Stendhal Achievements
 */
function stendhalDataIncludeAchievement($input, $argv, $parser) {
	$parsedInput = stendhalDataIncludeParseInput($input);

	$parser->disableCache();

	$res = '';
	connect();
	$Achievement = Achievement::getAchievement($parsedInput['name']);
	disconnect();
	if ($Achievement == NULL) {
		return '&lt;Achievement "' . htmlspecialchars($parsedInput['name']) . '" not found&gt;';
	}

	if (isset($argv['info']) && ($argv['info'] == 'icon')) {
		$res .= stendhalDataIncludeAchievementIconOnly($Achievement);
	} else {
		$res .= stendhalDataIncludeAchievementStats($Achievement, $argv);
	}

	$link = rewriteURL('/achievement/' . surlencode($Achievement->name) . '.html');
	$res = stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $parsedInput['display'], "stendhalAchievementLink", $res);

	return $res;
}

?>
