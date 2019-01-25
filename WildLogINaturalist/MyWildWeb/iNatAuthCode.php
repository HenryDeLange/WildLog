
<H1>iNaturalist Authentication Token</H1>
Use this token in WildLog to interact with your iNaturalist observations.
<br />
<br />
<H2><u>iNaturalist Token</u></H2>
<?php
$postData = array(
    'client_id' => '33639c729ed7cfaa1d6d23411c94c6225b60a54b8c2c65bdea2d0baa07e629c0',
    'client_secret' => '6c0225abd1edc16ad70a34a8e0fa185888706f38295e76844d23e65eeff1f0be',
    'code' => $_GET['code'],
    'redirect_uri' => 'http://www.mywild.co.za/wildlog/iNatAuthCode.php',
    'grant_type' => 'authorization_code'
);
// POST the request
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL,"https://www.inaturalist.org/oauth/token");
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($postData));
curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/x-www-form-urlencoded'));

// Receive server response ...
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$server_output = curl_exec($ch);
curl_close ($ch);

// Show the Token
echo "<b>";
$jsonObj = json_decode($server_output);
$key = "access_token";
echo  $jsonObj->$key;
echo "<b>";
?>
