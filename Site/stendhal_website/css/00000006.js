
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
//                                     netstat
//----------------------------------------------------------------------------

function initTracepath() {
	if (document.getElementById("traceip") == null) {
		return;
	}
	
	var progressIdx = 1;
	var progress = 1;
	var progressInterval = 0.2;

	$.ajax({url: "/index.php?id=content/scripts/api&method=traceroute&fast=1&ip=" + escape($('traceip').text()),
		dataType: 'html',
		success: function(data) {
		$('#traceresult1').html(data);
		$('#tracebox2').css('display', 'block');
		progressIdx = 2;
		progress = 1;
		progressInterval = 0.2;
	
		$.ajax({url: "/index.php?id=content/scripts/api&method=traceroute&fast=0&i="+ new Date().getTime()+"&ip=" + escape($('traceip').text()),
			dataType: 'html',
			success: function(data) {
			$('#traceresult2').html(data);
		}});
	}});

	setInterval(function() {
		if (progress == 50 || progress == 75) {
			progressInterval = progressInterval / 2;
		}
		if (progress < 95) {
			$("#progress" + progressIdx).css("width", progress + "%");
			progress = progress + progressInterval;
		}
	}, 100);
}


function initEditor() {
	if (typeof(CKEDITOR) != "undefined") {
		CKEDITOR.replace('editor', {
			toolbar: 'Full',
			toolbar_Full : [
			// See http://docs.cksource.com/CKEditor_3.x/Developers_Guide/Toolbar
			{ name: 'document', items : [ 'Source','-','Save'] },
			{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord' ] },
			{ name: 'editing', items : [ 'Undo','Redo','-','Find','Replace'] },
			{ name: 'insert', items : [ 'Link','Unlink','Anchor','-','Image','Table','HorizontalRule' ] },
			{ name: 'tools', items : [ 'Maximize'] },
			'/',
			{ name: 'styles', items : [ 'Format' ] },
			{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','-','Subscript','Superscript','-','TextColor','BGColor','-','RemoveFormat' ] },
			{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'] },
			],
			toolbarCanCollapse: false
			// Customize styles instead of Formats: http://docs.cksource.com/CKEditor_3.x/Howto/Styles_List_Customization
		});
		CKEDITOR.on( 'instanceReady', function( ev ) {
			ev.editor.dataProcessor.writer.selfClosingEnd = '>';
		});
		/* disabled because it ask on save, too.
		function beforeUnload(e) {
			if (CKEDITOR.instances.editor.checkDirty()) {
				return e.returnValue = "You'll loose the changes made in the editor.";
			}
		}
		if (window.addEventListener) {
			window.addEventListener('beforeunload', beforeUnload, false);
		} else {
			window.attachEvent( 'onbeforeunload', beforeUnload );
		}*/
	}

}

//----------------------------------------------------------------------------
//                                 Atlas
//----------------------------------------------------------------------------


function EuclideanProjection() {
	var EUCLIDEAN_RANGE = 4*256; // move markers outside the map area far out of the way
	this.pixelOrigin_ = new google.maps.Point(EUCLIDEAN_RANGE / 2, EUCLIDEAN_RANGE / 2);
	this.pixelsPerLonDegree_ = EUCLIDEAN_RANGE / 360;
	this.pixelsPerLonRadian_ = EUCLIDEAN_RANGE / (2 * Math.PI);
	this.scaleLat = 2;      // Height - multiplication scale factor
	this.scaleLng = 1;      // Width - multiplication scale factor
	this.offsetLat = 0;     // Height - direct offset +/-
	this.offsetLng = 0;     // Width - direct offset +/-
};

EuclideanProjection.prototype.fromLatLngToPoint = function(latLng, opt_point) {
	var point = opt_point || new google.maps.Point(0, 0);
	var origin = this.pixelOrigin_;
	point.x = (origin.x + (latLng.lng() + this.offsetLng ) * this.scaleLng * this.pixelsPerLonDegree_);
	point.y = (origin.y + (-1 * latLng.lat() + this.offsetLat ) * this.scaleLat * this.pixelsPerLonDegree_);
	return point;
};

EuclideanProjection.prototype.fromPointToLatLng = function(point) {
	var me = this;
	var origin = me.pixelOrigin_;
	var lng = (((point.x - origin.x) / me.pixelsPerLonDegree_) / this.scaleLng) - this.offsetLng;
	var lat = ((-1 *( point.y - origin.y) / me.pixelsPerLonDegree_) / this.scaleLat) - this.offsetLat;
	return new google.maps.LatLng(lat , lng, true);
};

function worldToLatLng(x, y) {
	var xw0 = 499616;
	var yw0 = 499744;
	var xwz = 501280;
	var ywz = 500896;

	var xl0 = 0;
	var yl0 = 0;
	var xlz = 208.15;
	var ylz = 144.2;

	var lx = (x - xw0) / (xwz - xw0) * (xlz - xl0) + xl0;
	var ly = (y - yw0) / (ywz - yw0) * (ylz - yl0) + yl0;
	return mapType.projection.fromPointToLatLng({x:lx, y:ly});
}


// http://www.netlobo.com/url_query_string_javascript.html
function gup(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );
	if (results == null) {
		return "";
	} else {
		return results[1];
	}
}

function initializeAtlas() {

	mapType = new google.maps.ImageMapType({
		getTileUrl: function(coord, zoom) {
			var y = coord.y;
			var x = coord.x;
			var tileRange = 1 << zoom;
			if (y < 0 || y >= tileRange) {
				return null;
			}
			if (x < 0 || x >= tileRange) {
				return null;
			}
//			return "http://localhost/map/" + zoom + "-" + coord.x + "-" + coord.y + ".png";
			return "http://arianne.sourceforge.net/stendhal/map/2/" + zoom + "-" + coord.x + "-" + coord.y + ".png";
		},
		tileSize: new google.maps.Size(256, 256),
		isPng: true,
		maxZoom: 6,
		minZoom: 1,
		name: 'Outside',
		credit: 'Stendhal'
	});
	mapType.projection = new EuclideanProjection(); 


	var mapOptions = {
		backgroundColor: "#5f9860",
		center: worldToLatLng(parseInt($("#data-center").attr("data-x")), parseInt($("#data-center").attr("data-y"))),
		noClear: true,
		zoom: parseInt($("#data-center").attr("data-zoom")),
		mapTypeControl: false,
		streetViewControl: false
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	map.mapTypes.set("outside", mapType);

	infowindow = new google.maps.InfoWindow({});

	map.setMapTypeId('outside');
	if ($("#data-me").length > 0) {
		var me = new google.maps.Marker({
			position: worldToLatLng(parseInt($("#data-me").attr("data-x")), parseInt($("#data-me").attr("data-y"))),
			map: map, title:"Me",
			icon: "/images/mapmarker/me.png"
			});
		addClickEventToMarker(map, me, {
				name: "Me",
				title: "Me",
				description: "I am here at " + $("#data-me").attr("data-zone")
					+ " (" + $("#data-me").attr("data-local-x") + ", " + $("#data-me").attr("data-local-y") + ")",
				url: "/account/mycharacters.html"
			});
	}
	var pois = $.parseJSON($("#data-pois").attr("data-pois"));
	wanted = decodeURI(gup("poi")).toLowerCase().split(",");
	for (var key in pois) {
		var poi = pois[key];
		if (($.inArray(poi.type.toLowerCase(), wanted) > -1)
			|| ($.inArray(poi.name.toLowerCase(), wanted) > -1)) {

			var marker = new google.maps.Marker({position: worldToLatLng(poi.gx, poi.gy),
				map: map, title: poi.name, icon: "/images/mapmarker/" + poi.type + ".png"});

			addClickEventToMarker(map, marker, poi);
		}
	}
}

function addClickEventToMarker(map, marker, poi) {
	google.maps.event.addListener(marker, 'click', function(x, y, z) {
		infowindow.setContent("<b><a target=\"_blank\" href=\""
				 + poi.url + "\">" 
				 + poi.title.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;")
				 + "</a></b><p>" + poi.description.replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;") + "</p>");
		infowindow.open(map, marker);
	});
}


//----------------------------------------------------------------------------
//                                    social popups
//----------------------------------------------------------------------------


// http://yensdesign.com/2008/09/how-to-create-a-stunning-and-smooth-popup-using-jquery/

var popupStatus = 0;  

function loadPopup(e){
	if (popupStatus == 0) {
		var popupHeight = $("#popup").height();  
		var popupWidth = $("#popup").width();  
		$("#popup").css({
			"position": "absolute",
			"top": e.offset().top - popupHeight / 2,  
			"left": e.offset().left - popupWidth / 2  
		});
		$("#backgroundPopup").css({  
			"height": document.documentElement.clientHeight
		});

		$("#backgroundPopup").css({"opacity": "0.7"});  
		$("#backgroundPopup").fadeIn("slow");
		$("#popup").fadeIn("slow");
		popupStatus = 1;
	}
}

function disablePopup(){  
	if (popupStatus == 1) {  
		$("#backgroundPopup").fadeOut("slow");  
		$("#popup").fadeOut("slow");
		popupStatus = 0;
	}
}

function initSocialMediaPopup() {
	$(".socialmedia").click(function(){
		popupHandler($(this));
		loadPopup($(this));
	});  
	$("#popupClose").click(function(){  
		disablePopup();  
	});  
	$("#backgroundPopup").click(function(){  
		disablePopup();  
	});  
	$(document).keypress(function(e){  
		if(e.keyCode==27 && popupStatus==1){  
			disablePopup();  
		}  
	});  
}

function popupHandler(e) {
	var socialLink = "http://stendhalgame.org/-" + e.attr("data-id");
	var message = e.attr("data-title");

    var html = ''
    	+ '<div class="socialbutton"><iframe src="https://www.facebook.com/plugins/like.php?href='+socialLink+'&amp;send=false&amp;layout=standard&amp;width=400&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=24" scrolling="no" frameborder="0" style="border:none; overflow:visible; width:400px; height:35px;" allowTransparency="true">Loading Facebook</iframe></div>'
//    	+ '<div id="fb-root"></div><div class="spaceafter"><div class="fb-like" data-href="'+socialLink+'" data-send="false" data-width="450" data-show-faces="false"></div></div>'
		+ '<div class="socialbutton"><div class="g-plusone" data-size="medium" data-href="'+socialLink+'">Loading Google+</div></div>'
		+ '<div class="socialbutton"><a href="https://twitter.com/share" class="twitter-share-button" data-url="' + socialLink + '" data-text="' + message + '" data-hashtags="stendhalgame">Loading Twitter</a></div>'
		+ '<div class="socialbutton"><a href="https://flattr.com/thing/333510/Faiumoni-e-V-" target="_blank"><img src="https://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0" /></a></div>'
		+ '<div><a href="https://stendhalgame.org/wiki/Two_clicks_for_more_privacy" target="_blank">Two clicks for more privacy.</a></div>'
		+ '<script async type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>'
		+ '<script async type="text/javascript" src="https://platform.twitter.com/widgets.js" id="twitter-wjs"></script>';
//    	+ '<script async type="text/javascript" src="https://connect.facebook.net/en_US/all.js#xfbml=1" id="facebook-jssdk"></script>'
    $('#popupContent').html(html);

    
}

//----------------------------------------------------------------------------
//                                       init
//----------------------------------------------------------------------------


$().ready(function() {
	$('#screenshotLink').click(function(event) {
		var left = Math.max(0, (screen.width-800)/2);
		var top = Math.max(0, (screen.height-550)/2);
		window.open("/images/screenshot/", "screenshot", "left="+left+",top="+top+",status=0,toolbar=0,location=0,menubar=0,directories=0,height=550,width=800,scrollbars=1")
		event.preventDefault();
		return false;
	});

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

	if (document.getElementById("map_canvas") != null) {
		initializeAtlas();
	}

	if (document.getElementById("gadget-redirect") != null) {
		window.top.location.href = $("#gadget-redirect").attr("href");
	}
	initTracepath();
	initEditor();
	initSocialMediaPopup();
});
