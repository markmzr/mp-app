// Get the item's latitude and longitude before submitting the form
$('#addItemForm').submit(function(event) {
    getLatLong(setLatLong);
    event.preventDefault();
    });

// Get the item's latitude and longitude using Google Maps API
function getLatLong(setLatLong) {
    var xhttp = new XMLHttpRequest();
    var zipCode = document.getElementById("zipCode").value;
    var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + ",US&sensor=false&key=" + apiKey;
    console.log(url);

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var jsonResponse = JSON.parse(this.responseText);
            var results = jsonResponse.results;
            var latitude = results[0].geometry.location.lat;
            var longitude = results[0].geometry.location.lng;
            setLatLong(latitude, longitude);
        }
        if (this.readyState == 4 && this.status == 400) {
            setLatLong(0, 0);
        }
    };

    xhttp.open("GET", url, true);
    xhttp.send();
}

// Add the latitude and longitude values to the form and submit it
function setLatLong(latitude, longitude) {
    var latitudeDiv = document.createElement("input");
    latitudeDiv.setAttribute("type", "text");
    latitudeDiv.setAttribute("name", "latitude");
    latitudeDiv.setAttribute("value", latitude);
    latitudeDiv.style.display = "none";
    document.getElementById("addItemForm").appendChild(latitudeDiv);

    var longitudeDiv = document.createElement("input");
    longitudeDiv.setAttribute("type", "text");
    longitudeDiv.setAttribute("name", "longitude");
    longitudeDiv.setAttribute("value", longitude);
    longitudeDiv.style.display = "none";
    document.getElementById("addItemForm").appendChild(longitudeDiv);

    document.getElementById("addItemForm").submit();
}


