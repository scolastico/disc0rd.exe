const xmlHttp = new XMLHttpRequest();
xmlHttp.open( "GET", "../api/v1/admin/getUsername", true );
xmlHttp.onreadystatechange = function() {
    if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
        if (JSON.parse(xmlHttp.responseText).status === "ok") {
            document.getElementById("username").textContent = JSON.parse(xmlHttp.responseText).username;
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttp.send( null );