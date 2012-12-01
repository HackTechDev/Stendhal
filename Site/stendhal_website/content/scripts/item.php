<?php

/**
 * Define the separator for decimals and for thousands to be used.
 * TODO: Remove the "MY_" prefix. It is just here to (hopefully)
 * make sure that there is no name conflict of any existing define.
 */

define('MY_DECIMAL_SEPARATOR', '.');
define('MY_THOUSANDS_SEPARATOR', ',');


/**
 * Format the number using (hard-coded) English locale with
 * the given number of digits. Terminating zeros and a possibly
 * terminating decimal point are removed as well.
 * TODO: Put this function to a global include and use it for all numbers.
 *
 * @param value float | integer
 * @param digits integer
 *
 * @return string
 */

function formatNumber($value, $digits = 6)
{
  $sNumber = number_format($value, $digits, MY_DECIMAL_SEPARATOR, MY_THOUSANDS_SEPARATOR);

  // $sNumber could possibly contain trailing zeros, e.g. '10,000.000000'.
  // Remove the trailing zero, and the decimal point, but no any more zeros.

  list($sBefore, $sAfter) = explode(MY_DECIMAL_SEPARATOR, $sNumber);

  if (($sAfter = rtrim($sAfter, '0')) === '') {

    // We have no fraction.

    return $sBefore;
  }

  return $sBefore . MY_DECIMAL_SEPARATOR . $sAfter;
}


/*

// This block is just a test for the function formatNumber().
// TODO: Move this somewhere else.

function check_formatNumber($value, $expected)
{
  if (($actual = formatNumber($value)) !== $expected) {
    echo 'formatNumber() failed for "' . $value . '" - "' . $actual . '" vs "' . $expected . '"' . "\n";
  }
}

check_formatNumber(1, '1');
check_formatNumber(1.000, '1');
check_formatNumber(1000, '1' . MY_THOUSANDS_SEPARATOR . '000');
check_formatNumber(1000.00, '1' . MY_THOUSANDS_SEPARATOR . '000');
check_formatNumber(100.0012, '100' . MY_DECIMAL_SEPARATOR . '0012');
exit;

*/

function renderAmount($amount) {
  $amount=str_replace("[","",$amount);
  $amount=str_replace("]","",$amount);
  list($min,$max)=explode(",",$amount);
  
  if($min!=$max) {
    return 'between ' . formatNumber($min) . ' and ' . formatNumber($max) . '.';
  }

  return 'exactly ' . formatNumber($min) . '.';
}


class ItemPage extends Page {
	private $class;
	private $name;
	private $items;
	private	$isExact;
	private $found;
	private $counter;

	public function __construct() {
		$this->name = preg_replace('/_/', ' ', $_REQUEST['name']);
		$this->class = preg_replace('/_/', ' ', $_REQUEST['class']);
		$this->isExact = isset($_REQUEST['exact']);
		$this->items=getItems();

		// does this name exist?
		$this->counter = 0;
		foreach($this->items as $m) {
			$x = ($m->name == $this->name || (!$this->isExact && strpos($m->name, $this->name) !== FALSE));
			$y = (($m->class == $this->class) || $this->class == 'all');
			if (($m->name == $this->name || (!$this->isExact && strpos($m->name, $this->name) !== FALSE)) && (($m->class == $this->class) || $this->class == 'all')) {
				$this->found = true;
				$this->counter++;
				$realClass = $m->class;
			}
		}
		if (isset($realClass) && ($this->counter == 1)) {
			$this->class = $realClass;
		}
	}

	public function writeHttpHeader() {
		global $protocol;
		if ($this->isExact && !$this->found) {
			header('HTTP/1.0 404 Not Found');
			return true;
		}


		if (($this->isExact || $this->counter==1) && (strpos($_REQUEST['class'], ' ') !== FALSE || strpos($_REQUEST['name'], ' ') !== FALSE || $_REQUEST['class'] == 'all')) {
			header('HTTP/1.0 301 Moved permanently.');
			header("Location: ".$protocol."://".$_SERVER['HTTP_HOST'].preg_replace("/&amp;/", "&", 
				rewriteURL('/item/'.surlencode($this->class).'/'.surlencode($this->name).'.html')));
			echo ($this->isExact || $this->counter==1);
			return false;
		}
		return true;
	}

	public function writeHtmlHeader() {
		echo '<title>Item '.htmlspecialchars($this->name).STENDHAL_TITLE.'</title>';
		if (!$this->found) {
			echo '<meta name="robots" content="noindex">'."\n";
		}
	}

	function writeContent() {

	
if ($this->isExact && !$this->found) {
	startBox("No such Item");
	?>
	There is no such item at Stendhal.<br>
	Please make sure you spelled it correctly.
	<?php
	endBox();
	return;
}

foreach($this->items as $m) {
  /*
   * If name of the creature match or contains part of the name.
   */
  if (($m->name==$this->name || (!$this->isExact && strpos($m->name, $this->name) !== FALSE)) && (($m->class == $this->class) || ($this->class == 'all'))) {
    startBox("Detailed information");
    ?>
    <div class="item">
      <div class="name"><?php echo ucfirst($m->name); ?></div>
      <div class="type">This item is of <?php echo $m->class ?> class</div>
      <img class="item" src="<?php echo $m->gfx; ?>" alt="">
      <div class="description">
        <?php 
          if($m->description=="") {
            echo 'No description. Would you like to <a href="http://sourceforge.net/tracker/?func=add&amp;group_id=1111&amp;atid=301111">write one</a>?';
          } else {
            echo $m->description;
          }
        ?>
      </div>
      
      <div class="table">
        <div class="title">Attributes</div>
  	      <?php

	// set initial values
		$minlevel=0;
		$level=0;
		$factor = 1;

	// get the min level if it has one
		foreach($m->attributes as $label=>$data) {
			if($label=="min_level") {
				$minlevel=$data;
				// did player fill in his level yet?
				if(!empty($_POST['level'])) {	
					$level = $_POST['level'];
					if ($level<$minlevel) {
						// scale factor for rate and def
						$factor= 1 - log(($level + 1) / ($minlevel + 1));
					}
				}
			}
		}
		foreach($m->attributes as $label=>$data) {
            ?>
            <div class="row">
              <div class="label"><?php echo strtoupper($label); ?></div>
              <div class="data"><?php echo $data; ?></div>
            </div>
            <?php
				if($label=="rate") {
					if($factor!=1) {
		 				$rate = ceil($data*$factor);
				?>
				<div class="label">EFFECTIVE RATE for player level <?php echo htmlspecialchars($level); ?> </div>
		    	<div class="data"><?php echo $rate; ?></div>
			<?php } 
			}
			if($label=="def") {
					if($factor!=1) {
		 				$def = floor($data/$factor);
				?>
				<div class="label">EFFECTIVE DEF for player level <?php echo  htmlspecialchars($level); ?></div>
				<div class="data"><?php echo $def; ?></div>
			<?php } 
			}
			if($label=="min_level") {
				?>
					<br>
					My level ...
					<form method="post" action="/item/<?php echo surlencode($m->class).'/'.surlencode($m->name); ?>.html">
						<input type="text" name="level" size="3" maxlength="3">
					<input type="submit" value="Check stats">
					</form>
				<?php 
			}
		}
	?>
	</div>

	<?php if (count($m->susceptibilities) > 0) {?>
	<div class="table">
		<div class="title">Resistances</div>
		<?php
		foreach($m->susceptibilities as $label=>$data) {
		?>
			<div class="row">
				<div class="label"><?php echo strtoupper($label); ?></div>
				<div class="data"><?php echo $data; ?>%</div>
			</div>
		<?php
		}
		?>
	</div>
	<?php }?>

      <div class="table">
        <div class="title">Dropped by</div>
          <div style="float: left; width: 100%;">
            <?php
          $monsters=getMonsters();
          foreach($monsters as $monster) {
            foreach($monster->drops as $k) {
              if($k["name"]==$m->name) {
              ?>
              <div class="row">
                <?php $monster->showImageWithPopup() ?>
                <span class="block label"><?php echo $monster->name; ?></span>
                <div class="data">Drops <?php echo renderAmount($k["quantity"]); ?></div>
                <div class="data">Probability: <?php echo formatNumber($k["probability"]); ?>%</div>
                <div style="clear: left;"></div>
              </div>
              <?php
              }
            } 
          }
          ?>
          </div>
          <div style="clear: left;"></div>
        </div>
	</div>
    <?php
    endBox();
		$this->includeJs();
  }
}

	}
}
$page = new ItemPage();
?>