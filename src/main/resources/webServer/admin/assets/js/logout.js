function fireSWAL(title, text, icon) {
    swal.fire({
        type: icon,
        title: title,
        text: text,
        showConfirmButton: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        allowOutsideClick: false
    });
}

function fireErrorSWAL(text) {
    fireSWAL("Oops...", text, "error");
}

function fireGenericErrorSWAL() {
    fireErrorSWAL("An internal error occurred!\n\nYou can try to reload the page or\nexecute 'disc0rd/admin' again!");
}

fireSWAL("Connection to API", "Please wait a few seconds!", "info");

const xmlHttp = new XMLHttpRequest();
xmlHttp.open( "GET", "../api/v1/guild/logout", true );
xmlHttp.onreadystatechange = function() {
    if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
        if (JSON.parse(xmlHttp.responseText).status === "ok") {
            fireSWAL("Success,", "logged successfully out!", "success");
        } else if (JSON.parse(xmlHttp.responseText).status === "error"){
            if (JSON.parse(xmlHttp.responseText).error === "no auth") {
                fireSWAL("Oops...", "You are not logged in!", "error");
            } else {
                fireGenericErrorSWAL();
            }
        }
    }
};
xmlHttp.send( null );