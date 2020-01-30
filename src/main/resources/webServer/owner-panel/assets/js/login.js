function fireSWAL(title, text, icon) {
    swal.fire({
        icon: icon,
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

function fireNotValidSWAL() {
    fireErrorSWAL("The given key (url) is not valid...\nYou need to request a new url by the bot!\nExecute 'disc0rd/owner-panel' again!");
}

function fireGenericErrorSWAL() {
    fireErrorSWAL("An internal error occurred!\n\nYou can try to reload the page or\nexecute 'disc0rd/owner-panel' again!");
}

fireSWAL("Logging in...", "Please wait a few seconds!", "info");

const hash = window.location.hash.substr(1);
if (hash.length === 16) {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "POST", "../api/v1/admin/login", true );
    xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            if (JSON.parse(xmlHttp.responseText).status === "ok") {
                fireSWAL("Success,", "you will be redirected in 3 seconds!", "success")
                setTimeout(function() {
                    window.location.replace("index.html");
                }, 3000);
            } else if (JSON.parse(xmlHttp.responseText).status === "error"){
                if (JSON.parse(xmlHttp.responseText).error === "no auth") {
                    fireNotValidSWAL();
                } else {
                    fireGenericErrorSWAL();
                }
            }
        }
    };
    xmlHttp.send( "key=" + hash );
} else {
    fireNotValidSWAL();
}