<?php
if (!defined('MEDIAWIKI')) {
	die("<b>Stendhal Data Include</b> is a MediaWiki extension not intended to be used on its own.");
}

require_once($IP.'/../configuration.php');
require_once($IP.'/../scripts/xml.php');
require_once($IP.'/../scripts/monsters.php');
require_once($IP.'/../scripts/urlrewrite.php');


/**
 * helper function to read the creature with this name using the Stendhal website code
 */
function getCreatureByName($name) {
	$name = strtolower($name);
	$creatures = getMonsters();

	foreach ($creatures as $creature) {
		if (strtolower($creature->name) == $name) {
			return $creature;
		}
	}
	return NULL;
}


/**
 * reads the icon of a Stendhal creatures
 */
function stendhalDataIncludeCreatureIconOnly($creature) {
	$res = '<span class="stendhalCreatureIcon"><a href="'.rewriteURL('/creature/'.surlencode($creature->name).'.html').'">';
	$res .= '<img src="' . htmlspecialchars($creature->gfx) . '" />';
	$res .= '</a></span>';
	return $res;
}

/**
 * reads stats and optionally the icon and the description of Stendhal creatures
 */
function stendhalDataIncludeCreatureStats($creature, $argv) {
	$res = '<div class="stendhalCreature"><span class="stendhalCreatureIconNameBanner">';

	if (!isset($argv['info'])) {
		$res .= stendhalDataIncludeCreatureIconOnly($creature);
	}

	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<a href="'.rewriteURL('/creature/'.surlencode($creature->name).'.html').'">';
		$res .= $creature->name;
		$res .= '</a>';
	}
	$res .= '</span>';
		
	if (!isset($argv['info']) || ($argv['info'] == 'stats')) {
		$res .= '<br />';
		$res .= 'Class: ' . htmlspecialchars(ucfirst($creature->class)) . '<br />';
		$res .= 'Level: ' . htmlspecialchars($creature->level) . '<br />';
		foreach($creature->attributes as $label=>$data) {
			$res .= htmlspecialchars(ucfirst($label)) . ': ' . htmlspecialchars($data) . '<br />';
		}
	}
	if (!isset($argv['info'])) {
		$res .= '<br />' . $creature->description . '<br />';
	}

	$res .= '</div>';
	return $res;
}

/**
 * includes data about Stendhal creatures
 */
function stendhalDataIncludeCreature($input, $argv, $parser) {
	$parsedInput = stendhalDataIncludeParseInput($input);

	$res = '';
	$creature = getCreatureByName($parsedInput['name']);
	if ($creature == NULL) {
		return '&lt;creature "' . htmlspecialchars($parsedInput['name']) . '" not found&gt;';
	}

	if (isset($argv['info']) && ($argv['info'] == 'icon')) {
		$res .= stendhalDataIncludeCreatureIconOnly($creature);
	} else {
		$res .= stendhalDataIncludeCreatureStats($creature, $argv);
	}

	$link = rewriteURL('/creature/' . surlencode($creature->name) . '.html');
	$res = stendhalDataIncludeAddMoveoverBoxIfDesired($argv, $link, $parsedInput['display'], "stendhalCreatureLink", $res);

	return $res;
}

?>
