
// ----------------------------------------------------------------------------
//                               changepassword.php
// ----------------------------------------------------------------------------

function changePasswordCheckForm() {
	var old = document.getElementById("pass");
	if ((old != null) && (old.value.length < 1)) {
		old.focus();
		alert("Please enter your old password.");
		return false;
	}

	var pw = document.getElementById("newpass");
	if (pw.value.length < 6) {
		pw.focus();
		alert("Your new password needs to be at least 6 letters long.");
		return false;
	}

	if (pw.value == document.getElementById("sessionUsername").value) {
		pw.focus();
		alert("Your password must not be your username.");
		return false;
	}

	var pr = document.getElementById("newpass_retype");
	if (pw.value != pr.value) {
		pw.focus();
		alert("Your password and repetition do not match.");
		return false;
	}

	return true;
}






//----------------------------------------------------------------------------
//                             createAccount.php
//----------------------------------------------------------------------------



function createAccountValidateMinLength(field) {
	if (field.value.length >= 6) {
		document.getElementById(field.id + "warn").innerHTML = "";
		minLengthOnceReached = true;
		return true;
	} else {
		if (minLengthOnceReached) {
			document.getElementById(field.id + "warn").innerHTML = "Must be at least 6 letters.";
		}
	}
	return false;
}

function createAccountValidateMinLengthFail(field) {
	if (field.value.length < 6) {
		document.getElementById(field.id + "warn").innerHTML = "Must be at least 6 letters.";
	}
}

function createAccountValidateMinLengthOk(field) {
	if (field.value.length >= 6) {
		document.getElementById(field.id + "warn").innerHTML = "";
		return true;
	}
	return false;
}

var lastRequestedName = "";
var minLengthOnceReached = false;
function createAccountNameChanged(field) {
	field.value = field.value.toLowerCase().replace(/[^a-z]/g,"");
	if (lastRequestedName != field.value) {
		lastRequestedName = field.value;
		var res = createAccountValidateMinLength(field);
		if (res) {
			$.getJSON(document.getElementById("serverpath").value + "/index.php?id=content/scripts/api&method=isNameAvailable&param=" + escape(lastRequestedName), function(data) {
				if (lastRequestedName == data.name) {
					if (data.result) {
						document.getElementById(field.id + "warn").innerHTML = "";
					} else {
						document.getElementById(field.id + "warn").innerHTML = "This name is not available.";
					}
				}
			});
		}
	}
}

function createAccountBlurName(field) {
	createAccountValidateMinLengthFail(field);
	createAccountNameChanged(field);
}

function createAccountCheckForm() {
	var name = document.getElementById("name");
	if (name.value.length < 6) {
		name.focus();
		alert("Your account name needs to be at least 6 letters long.");
		return false;
	}

	var pw = document.getElementById("pw");
	if (pw.value.length < 6) {
		pw.focus();
		alert("Your password needs to be at least 6 letters long.");
		return false;
	}

	var pw = document.getElementById("pw");
	if (name.value == pw.value) {
		pw.focus();
		alert("Your password must not be your username.");
		return false;
	}

	var pr = document.getElementById("pr");
	if (pw.value != pr.value) {
		pw.focus();
		alert("Your password and repetition do not match.");
		return false;
	}

	return true;
}







//----------------------------------------------------------------------------
//                        createcharacter.php
//----------------------------------------------------------------------------



createCharacterFaceOffset = 2;
createCharacterMaxOutfit = [44, 21, 15, 53];


createCharacterOutfitNames = ["hair", "head", "player_base", "dress"];

function createCharacterDown(i) {
	currentOutfit[i]--;
	if (currentOutfit[i] < 0) {
		currentOutfit[i] = createCharacterMaxOutfit[i] - 1;
	}
	createCharacterUpdate(i);
}

function createCharacterUp(i) {
	currentOutfit[i] = (currentOutfit[i] + 1) % createCharacterMaxOutfit[i];
	createCharacterUpdate(i);
}

function formatNumber(i) {
	if (i < 10) {
		return "0" + i;
	} else {
		return ""+i;
	}
}

function createCharacterUpdate(i) {
	document.getElementById("outfit" + i).style.backgroundImage = "url('/data/sprites/outfit/" + createCharacterOutfitNames[i] + "_" + currentOutfit[i] + ".png')";
	outfitCode = formatNumber(currentOutfit[0]) + formatNumber(currentOutfit[1]) + formatNumber(currentOutfit[3]) + formatNumber(currentOutfit[2]);
	document.getElementById("outfitcode").value = outfitCode;
	document.getElementById("canvas").style.backgroundImage = "url('/createoutfit.php?offset=" + createCharacterFaceOffset + "&outfit=" + outfitCode + "')";
}

function createCharacterInit() {
	createCharacterUpdateAll();
	self.focus();
	document.getElementById("name").focus();
}

function createCharacterUpdateAll() {
	for (i = 0; i < 4; i++) {
		document.getElementById("outfit" + i).style.backgroundImage = "url('/data/sprites/outfit/" + createCharacterOutfitNames[i] + "_" + currentOutfit[i] + ".png')";
	}
	var outfitCode = formatNumber(currentOutfit[0]) + formatNumber(currentOutfit[1]) + formatNumber(currentOutfit[3]) + formatNumber(currentOutfit[2]);
	document.getElementById("outfitcode").value = outfitCode;
	document.getElementById("canvas").style.backgroundImage = "url('/createoutfit.php?offset=" + createCharacterFaceOffset + "&outfit=" + outfitCode + "')";
}

function createCharacterTurn(i) {
	createCharacterFaceOffset = (createCharacterFaceOffset + i) % 4;
	if (createCharacterFaceOffset < 0) {
		createCharacterFaceOffset = 3;
	}
	var cssOffset = 4 - createCharacterFaceOffset;

	for (i = 0; i < 4; i++) {
		document.getElementById("outfit" + i).style.backgroundPosition = "0 " + (cssOffset * 64) + "px";
	}
	outfitCode = formatNumber(currentOutfit[0]) + formatNumber(currentOutfit[1]) + formatNumber(currentOutfit[3]) + formatNumber(currentOutfit[2]);
	document.getElementById("canvas").style.backgroundImage = "url('/createoutfit.php?offset=" + createCharacterFaceOffset + "&outfit=" + outfitCode + "')";
}

function createCharacterValidateMinLength(field) {
	if (field.value.length >= 6) {
		document.getElementById("warn").innerHTML = "&nbsp;";
		createCharacterMinLengthOnceReached = true;
		return true;
	} else {
		if (createCharacterMinLengthOnceReached) {
			document.getElementById("warn").innerHTML = "Must be at least 6 letters.";
		}
	}
	return false;
}

var createCharacterLastRequestedName = "";
var createCharacterMinLengthOnceReached = false;
function createCharacterNameChanged(field) {
	field.value = field.value.toLowerCase().replace(/[^a-z]/g,"");
	if (createCharacterLastRequestedName != field.value) {
		createCharacterLastRequestedName = field.value;
		var res = createCharacterValidateMinLength(field);
		if (res) {
			var serverpath = document.getElementById("serverpath").value;
			var username = document.getElementById("sessionUsername").value;
			
			$.getJSON(serverpath + "/index.php?id=content/scripts/api&method=isNameAvailable&ignoreAccount=" + escape(username) + "&param=" + escape(createCharacterLastRequestedName), function(data) {
				if (createCharacterLastRequestedName == data.name) {
					if (data.result) {
						document.getElementById("warn").innerHTML = "&nbsp;";
					} else {
						document.getElementById("warn").innerHTML = "This name is not available.";
					}
				}
			});
		}
	}
}


function createCharacterCheckForm() {
	var name = document.getElementById("name");
	if (name.value.length < 6) {
		name.focus();
		alert("Your character name needs to be at least 6 letters long.");
		return false;
	}
	return true;
}


//----------------------------------------------------------------------------
//                                       init
//----------------------------------------------------------------------------


$().ready(function() {
	$('#changePasswordForm').submit(function () {
		return changePasswordCheckForm();
	});

	if (document.getElementById("createAccountForm") != null) {
		$('#createAccountForm #name').change(function() {
			return createAccountNameChanged(this);
		});
		$('#createAccountForm #name').keyup(function() {
			return createAccountNameChanged(this);
		});
		$('#createAccountForm #name').blur(function() {
			return createAccountBlurName(this);
		});

		$('#createAccountForm #pw').change(function() {
			return createAccountValidateMinLengthOk(this);
		});
		$('#createAccountForm #pw').keyup(function() {
			return createAccountValidateMinLengthOk(this);
		});
		$('#createAccountForm #pw').blur(function() {
			return createAccountValidateMinLengthFail(this);
		});
	}

	if (document.getElementById("createCharacterForm") != null) {
		currentOutfit = document.getElementById("currentOutfit").value.split(",");
		$('#createCharacterForm #name').change(function() {
			return createCharacterNameChanged(this);
		});
		$('#createCharacterForm #name').keyup(function() {
			return createCharacterNameChanged(this);
		});
		$('#createCharacterForm .turn').click(function() {
			return createCharacterTurn(parseInt(this.getAttribute("data-offset")));
		});
		$('#createCharacterForm .prev').click(function() {
			return createCharacterDown(parseInt(this.getAttribute("data-offset")));
		});
		$('#createCharacterForm .next').click(function() {
			return createCharacterUp(parseInt(this.getAttribute("data-offset")));
		});
		createCharacterInit();
	}

	$('.overliblink').tooltip({ 
		bodyHandler: function() { 
			return $(this).attr("data-popup");
		},
		showURL: false,
		track: true,
		delay: 0
	});

	$('#irclog-toggle-ircstatus-span').show();
	$('.ircstatus').hide();
	$('#irclog-toggle-ircstatus').click(function() {
		if ($(this).attr('checked')) {
			$('.ircstatus').show();
		} else {
			$('.ircstatus').hide();
		}
	});
	$('#irclog-toggle-ircstatus').change(function() {
		if ($(this).attr('checked')) {
			$('.ircstatus').show();
		} else {
			$('.ircstatus').hide();
		}
	});
});
