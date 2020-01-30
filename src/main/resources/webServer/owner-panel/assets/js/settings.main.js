function sendSettings() {
    if (document.getElementById("w2gDefaultPlayback").value === "") {
        swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttp_settings_send = new XMLHttpRequest();
    xmlHttp_settings_send.open( "POST", "../api/v1/admin/saveSettings", true );
    xmlHttp_settings_send.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp_settings_send.onreadystatechange = function() {
        if (xmlHttp_settings_send.readyState === 4 && xmlHttp_settings_send.status === 200) {
            if (JSON.parse(xmlHttp_settings_send.responseText).status === "ok") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true,
                    onOpen: (toast) => {
                        toast.addEventListener('mouseenter', Swal.stopTimer)
                        toast.addEventListener('mouseleave', Swal.resumeTimer)
                    }
                });
                Toast.fire({
                    icon: 'success',
                    title: 'Saved!'
                });
            } else {
                window.location.replace("logout.html");
            }
        }
    };
    xmlHttp_settings_send.send( "w2gDefaultPlayback=" + encodeURI(document.getElementById("w2gDefaultPlayback").value) );
}

const xmlHttp_settings = new XMLHttpRequest();
xmlHttp_settings.open( "GET", "../api/v1/admin/getSettings", true );
xmlHttp_settings.onreadystatechange = function() {
    if (xmlHttp_settings.readyState === 4 && xmlHttp_settings.status === 200) {
        if (JSON.parse(xmlHttp_settings.responseText).status === "ok") {
            document.getElementById("w2gDefaultPlayback").value = JSON.parse(xmlHttp_settings.responseText).settings.w2gDefaultPlayback;
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttp_settings.send( "w2gDefaultPlayback=" + encodeURI(document.getElementById("w2gDefaultPlayback").value) );
