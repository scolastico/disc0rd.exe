const huebee = new Huebee(".color-input", {
    notation: "hex",
    setBGColor: false,
    saturations: 1
});

let key = null;

function fireSWAL(title, text, icon) {
    swal({
        icon() {return icon;},
        title() {return title;},
        text() {return text;},
        button: false,
        closeOnClickOutside: false,
        closeOnEsc: false,
        closeOnScroll: false
    });
}

function fireErrorSWAL(text) {
    fireSWAL("Oops...", text, "error");
}

function fireNotValidSWAL() {
    fireErrorSWAL("The given key (url) is not valid...\nYou need to request a new url by the bot!\nExecute 'disc0rd/color' again!");
}

function fireGenericErrorSWAL() {
    fireErrorSWAL("An internal error occurred!\n\nYou can try to reload the page or\nexecute 'disc0rd/color' again!");
}

$(document).ready(function() {
    $("#color").css("background-color","rgb(255,255,255)");
    $("#colorBG").css("background-color","rgb(255,255,255)");
    const hash = window.location.hash.substr(1);
    if (hash.length === 16) {
        try {
            const xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "GET", "../api/v1/color-name/isActive/" + hash, true );
            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
                    if (JSON.parse(xmlHttp.responseText).status === "ok") {
                        key = hash;
                    } else {
                        fireNotValidSWAL();
                    }
                }
            };
            xmlHttp.send( null );
        } catch (e) {
            fireGenericErrorSWAL();
        }
    }
});

function hexToRgb(hex) {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16)
    } : null;
}

function rgbStringToRgb(col)  {
    if(col.charAt(0) === "r") {
        col=col.replace("rgb(", "").replace(")", "").split(",");
        return {
            r: parseInt(col[0], 10),
            g: parseInt(col[1], 10),
            b: parseInt(col[2], 10)
        };
    }
    return null;
}

let scheduler = null;
let selectedColor = "rgb(255,255,255)";

function calculateFade(oldColor, newColor, step) {
    if (oldColor !== newColor) { if (newColor > oldColor) {
        return Math.round((((newColor - oldColor) / 20) * step) + oldColor);
    } else {
        return Math.round(oldColor - (((oldColor - newColor) / 20) * step));
    } }
    return newColor;
}

function doFadeStep(currentColorBG, currentColorField, finalColorBG, finalColorField, step) {
    let currentFieldRGB = rgbStringToRgb(currentColorField);
    let finalFieldRGB = rgbStringToRgb(finalColorField);
    const fieldR = calculateFade(currentFieldRGB.r, finalFieldRGB.r, step);
    const fieldG = calculateFade(currentFieldRGB.g, finalFieldRGB.g, step);
    const fieldB = calculateFade(currentFieldRGB.b, finalFieldRGB.b, step);
    let currentBackgroundRGB = rgbStringToRgb(currentColorBG);
    let finalBackgroundRGB = rgbStringToRgb(finalColorBG);
    const backgroundR = calculateFade(currentBackgroundRGB.r, finalBackgroundRGB.r, step);
    const backgroundG = calculateFade(currentBackgroundRGB.g, finalBackgroundRGB.g, step);
    const backgroundB = calculateFade(currentBackgroundRGB.b, finalBackgroundRGB.b, step);
    const field = $("#color");
    const grayScale = Math.abs(255-(Math.round((fieldR+fieldG+fieldB)/3)));
    field.css("color", "rgb("+grayScale+","+grayScale+","+grayScale+")");
    field.css("background-color","rgb("+fieldR+","+fieldG+","+fieldB+")");
    $("#colorBG").css("background-color","rgb("+backgroundR+","+backgroundG+","+backgroundB+")");
}

function onColorChange() {
    const rgbColor = hexToRgb(document.getElementById("color").value);
    let finalColorBG = "rgb(255,255,255)";
    let finalColorField = "rgb(255,255,255)";
    if (scheduler !== null) {
        clearInterval(scheduler);
        scheduler = null;
    }
    if (rgbColor !== null) {
        finalColorBG = "rgb(" + rgbColor.r + "," + rgbColor.g + "," + rgbColor.b + ")";
        rgbColor.r += -25;
        if (rgbColor.r < 0) {rgbColor.r = 0;}
        rgbColor.g += -25;
        if (rgbColor.g < 0) {rgbColor.g = 0;}
        rgbColor.b += -25;
        if (rgbColor.b < 0) {rgbColor.b = 0;}
        finalColorField = "rgb(" + rgbColor.r + "," + rgbColor.g + "," + rgbColor.b + ")";
    }
    let step = 0;
    selectedColor = finalColorBG;
    const currentColorBG = document.body.style.backgroundColor;
    const currentColorField = document.getElementById("color").style.backgroundColor;
    scheduler = setInterval(() => {
        if (step !== 20) {
            step++;
            doFadeStep(currentColorBG, currentColorField, finalColorBG, finalColorField, step);
        } else {
            clearInterval(scheduler);
            scheduler = null;
        }
    }, 25);
}

$("#input").on("input", function(e) {
    onColorChange();
});

huebee.on( "change", function( color, hue, sat, lum ) {
    onColorChange();
});

function rgb2hex(rgb){
    rgb = rgb.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i);
    return (rgb && rgb.length === 4) ? "#" +
        ("0" + parseInt(rgb[1],10).toString(16)).slice(-2) +
        ("0" + parseInt(rgb[2],10).toString(16)).slice(-2) +
        ("0" + parseInt(rgb[3],10).toString(16)).slice(-2) : "#ffffff";
}

function submitColor() {
    document.getElementById("form").setAttribute("onsubmit", "return false;");
    document.getElementById("submit").classList.add("loading");
    if (key !== null) {
        try {
            const xmlHttp = new XMLHttpRequest();
            xmlHttp.open( "POST", "../api/v1/color-name/change/" + key, true );
            xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
                    const response = JSON.parse(xmlHttp.responseText);
                    if (response.status === "ok") {
                        fireSWAL("Nice!", "You're color is set on discord! Take a look!", "success");
                    } else if (response.status === "error") {
                        if (response.error === "color not supported") {
                            document.getElementById("form").setAttribute("onsubmit", "submitColor(); return false;");
                            document.getElementById("submit").classList.remove("loading");
                            swal({
                                icon: "warning",
                                title: "Oops...",
                                text: "This color is not supported! Try again with an other color!"
                            });
                        } else if (response.error === "key not valid") {
                            fireNotValidSWAL();
                        } else {
                            fireGenericErrorSWAL();
                        }
                    } else {
                        fireGenericErrorSWAL();
                    }
                } else {
                    fireGenericErrorSWAL();
                }
            };
            xmlHttp.send( "color=" + rgb2hex(selectedColor));
        } catch (e) {
            fireGenericErrorSWAL();
        }
    } else {
        fireNotValidSWAL();
    }
}