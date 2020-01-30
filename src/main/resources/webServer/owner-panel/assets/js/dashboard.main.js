const xmlHttpDashboard = new XMLHttpRequest();
xmlHttpDashboard.open( "GET", "../api/v1/admin/getStatus", true );
xmlHttpDashboard.onreadystatechange = function() {
    if (xmlHttpDashboard.readyState === 4 && xmlHttpDashboard.status === 200) {
        if (JSON.parse(xmlHttpDashboard.responseText).status === "ok") {
            document.getElementById("serverCount").textContent = JSON.parse(xmlHttpDashboard.responseText).statusInfo.serverCount;
            document.getElementById("executedCommands").textContent = JSON.parse(xmlHttpDashboard.responseText).statusInfo.executedCommands;
            JSON.parse(xmlHttpDashboard.responseText).statusInfo.errorLog.forEach(function(item, index, array) {
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
xmlHttpDashboard.send( null );