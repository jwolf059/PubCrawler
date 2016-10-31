<?PHP
ini_set('display_errors', '1');
error_reporting(E_ALL);
$command = $_GET['cmd'];

	// Connect to the Database
	$dsn = 'mysql:host=cssgate.insttech.washington.edu;dbname=jwolf059';
    	$username = 'jwolf059';
    	$password = 'kupevjun';

    	try {
        	$db = new PDO($dsn, $username, $password);
	        	
			if ($command == "pub") {
				$select_sql = 'SELECT * FROM Establishments e JOIN Address a ON e.address_idAddress = a.idAddress;'
				$pubs_query = $db->query($select_sql);
				$pubs = $pubs_query->fetchAll(PDO::FETCH_ASSOC);
				if ($pubs) {	
	   				echo json_encode($pubs);
				}
			}

    	} catch (PDOException $e) {
        	$error_message = $e->getMessage();
        	echo 'There was an error connecting to the database.';
			echo $error_message;
        	exit();
    	}

?>
