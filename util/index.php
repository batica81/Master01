<?php 
header('Content-Type: application/json');


$data = json_decode(file_get_contents('php://input'), true);

$username = (empty($data["Username"])? "default_username" : $data["Username"]);
$password = (empty($data["Password"])? "default_password" : $data["Password"]);


$jsondata = array (
  0 => 
  array (
    'id' => '3079',
    'username' => $username,
    'password' => $password,
    'name' => 'Vojislav Ristivojevic',
    'email' => 'batica@gmail.com',
    'bank_info' => 
    array (
      'Broj kreditne kartice' => '3787 3449 3671 5000',
      'Broj racuna' => '551-1545661-25',
    ),
    'phone' => '38163555333',
    'website' => 'tor64.duckdns.org',
    'company' => 
    array (
      'name' => 'Scripttic',
      'catchPhrase' => 'Multi-layered client-server neural-net',
      'bs' => 'harness real-time e-markets',
    ),
  ),
);


echo json_encode($jsondata);


