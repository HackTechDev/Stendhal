<?php
/*
Copyright (C) 2011 Faiumoni e. V.

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

$wgExtensionFunctions[] = "sourceforgeTrackerIncludeSetup";
$wgExtensionCredits['other'][] = array(
	'version' => '0.1',
	'name' => 'Sourceforge Tracker Include',
	'author' => 'Hendrik Brummermann',
	'url' => 'http://arianne.sf.net',
	'description' => 'Includes results from a sourceforge tracker search'
);

/**
* setup the parser by telling it about the tags we can
* handle and include the required java script
*/
function sourceforgeTrackerIncludeSetup() {
	global $wgParser, $wgScriptPath, $wgOut;
	$wgParser->setHook('tracker', 'sourceforgeTrackerInclude');
}

function sourceforgeTrackerInclude($input, $argv, $parser) {
	$parser->disableCache();

	$ids = urlencode($input);
	$url = 'http://sourceforge.net/search/index.php?group_id=1111&type_of_search=artifact&q=&artifact_id='.$ids.'&limit=100';

	// get search result
	$data = file_get_contents($url);

	// strip head and foot
	$needle = '<!-- 0 -->'; //<caption>Search Results</caption>';
	$pos = strpos($data, $needle) + strlen($needle);
	$data = substr($data, $pos, -1);
	$needle = '</table>';
	$pos = strpos($data, $needle) + strlen($needle);
	$data = substr($data, 0, $pos);

	// fix links
	$data = preg_replace('|<a href="|', '<a href="http://sourceforge.net', $data);

	// add heading
	$prefix = '<table class="prettytable sortable"><tr><th>Prio</th><th>ID</th><th>Tracker</th><th>Summary</th><th>Assignee</th><th>Submitter</th><th>Status</th><th>Opened</th></tr>';
	$data = $prefix.$data;

	return $data;
}
