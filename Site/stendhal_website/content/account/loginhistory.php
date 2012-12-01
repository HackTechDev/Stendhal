<?php
/*
 Stendhal website - a website to manage and ease playing of Stendhal game
 Copyright (C) 2008  Miguel Angel Blanch Lardin
 Copyright (C) 2008-2010 The Arianne Project

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

class LoginHistoryPage extends Page {

	public function writeHtmlHeader() {
		echo '<meta name="robots" content="noindex">'."\n";
		echo '<title>Login History'.STENDHAL_TITLE.'</title>';
	}

	function writeContent() {
		if(!isset($_SESSION['account'])) {
			startBox("Login History");
			echo '<p>Please <a href="'.STENDHAL_LOGIN_TARGET.'/index.php?id=content/account/login&amp;url='.rewriteURL('/account/history.html').'">login</a> to view your personal login history.</p>';
			endBox();
		} else {
			$this->printLoginHistory();
		}
	}
	
	function printLoginHistory() {
		$playerId = $_SESSION['account']->id;
		$events = PlayerLoginEntry::getLoginHistory($playerId);

		startBox('Login history');

		echo '<p>This is a list of your most recent logins and password changes. '
			.'If you suspect unauthorized access to your account, please '
			. '<a href="'.rewriteURL('/account/change-password.html').'">change your password</a>'
			.' immediately and contact <code>/support</code> in game.</p>';

		echo '<table class="prettytable"><tr><th>server time</th><th>ip-address</th><th>service</th><th>event</th><th>result</th></tr>';
		foreach ($events as $entry) {
			$timedate = htmlspecialchars($entry->timedate);
			echo '<tr><td>'.$timedate.'</td><td>'.htmlspecialchars($entry->address)
				.'</td><td>'.htmlspecialchars($entry->service)
				.'</td><td>'.htmlspecialchars($entry->eventType)
				.'</td><td>';
			if ($entry->success == 1) {
				echo '<span style="color:#0A0">OK</span>';
			} else {
				echo '<span style="color:#A00">FAIL</span>';
			}
			echo '</td></tr>';
		}
		echo '</table>';
		endBox(); 

	}
}
$page = new LoginHistoryPage();
?>