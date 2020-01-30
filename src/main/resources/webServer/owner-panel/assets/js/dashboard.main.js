const xmlHttp_dashboard = new XMLHttpRequest();
xmlHttp_dashboard.open( "GET", "../api/v1/admin/getStatus", true );
xmlHttp_dashboard.onreadystatechange = function() {
    if (xmlHttp_dashboard.readyState === 4 && xmlHttp_dashboard.status === 200) {
        if (JSON.parse(xmlHttp_dashboard.responseText).status === "ok") {
            document.getElementById("serverCount").textContent = JSON.parse(xmlHttp_dashboard.responseText).statusInfo.serverCount;
            document.getElementById("executedCommands").textContent = JSON.parse(xmlHttp_dashboard.responseText).statusInfo.executedCommands;
            JSON.parse(xmlHttp_dashboard.responseText).statusInfo.errorLog.forEach(function(item, index, array) {
                let dateObj = new Date(item.time * 1000);
                let utcString = dateObj.toUTCString();
                document.getElementById("errorLog").innerHTML += "\n" +
                    "                                <tr>\n" +
                    "                                    <td>" + utcString + "</td>\n" +
                    "                                    <td>" + decodeURI(item.error) + "</td>\n" +
                    "                                </tr>";
            });
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttp_dashboard.send( null );