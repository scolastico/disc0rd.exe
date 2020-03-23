const xmlHttp = new XMLHttpRequest();
xmlHttp.open( "GET", "../api/v1/guild/info", true );
xmlHttp.onreadystatechange = function() {
    if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
        if (JSON.parse(xmlHttp.responseText).status === "ok") {
            document.getElementById("username").textContent = decodeURIComponent(JSON.parse(xmlHttp.responseText).name).replace(/\+/g, ' ');
            if(JSON.parse(xmlHttp.responseText).image !== "null") {
                document.getElementById("serverIcon").src = decodeURIComponent(JSON.parse(xmlHttp.responseText).image);
            }
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttp.send( null );