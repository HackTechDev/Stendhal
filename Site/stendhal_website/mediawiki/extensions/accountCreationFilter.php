<?php

function acfUserCreateForm($template) {
        $_SESSION['acf_wiki_timestamp'] = time();
        return true;
}

function acfAbortNewAccount($user, $message) {
        if (!isset($_SESSION) || !isset($_SESSION['acf_wiki_timestamp']) || $_SESSION['acf_wiki_timestamp'] + 3 > time()) {
                $message = 'Automatic SPAM filter triggered, please try again later.';
                return false;
        }
        return true;
}

function acfPreventTalkPageWithoutMainPage($editpage) {
	global $wgRequest, $wgUser, $wgTitle;

	// user may not create their own user talk page without their user main page
	if (($wgTitle->getNamespace() == NS_USER_TALK)
		&& (!$wgTitle->exists())
		&& ($wgTitle->getText() == $wgUser->getName())
		&& (!Title::makeTitle($wgTitle->getSubjectNsText(), $wgUser->getName())->exists())) {
		
		$editpage->spamPageWithContent();
		return false;
	}
	return true;
}

$wgHooks['UserCreateForm'][] = 'acfUserCreateForm';
$wgHooks['AbortNewAccount'][] = 'acfAbortNewAccount';
$wgHooks['EditPage::attemptSave'][] = 'acfPreventTalkPageWithoutMainPage';
