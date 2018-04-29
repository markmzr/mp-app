setCondition();
setDistance();
setSort();

$('#distanceForm').submit(function() {
    updateDistance();
    return false;
    });

// Set the item conditions that are selected in the side menu based on the condition URL parameter
function setCondition() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var condition = parameters.get("condition");

    if (condition == "all") {
        document.getElementById("new").checked = false;
        document.getElementById("used").checked = false;
    }
    else if (condition == "any") {
        document.getElementById("new").checked = true;
        document.getElementById("used").checked = true;
    }
    else if (condition == "new") {
        document.getElementById("new").checked = true;
    }
    else if (condition == "used") {
        document.getElementById("used").checked = true;
    }
}

// Set the items that are displayed in the search results based on the distance URL parameter
function setDistance() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);

    if (parameters.has("miles") && parameters.has("zipCode")) {
        // Get latitude and longitude of the user provided zip code from Google API
        var xhttp = new XMLHttpRequest();
        var zipCode = parameters.get("zipCode");
        var apiKey = "AIzaSyBOLZjanrFdnJbMRmDKr1ciQqRPRMfJg_Y";
        var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + "&sensor=false&key=" + apiKey;

        xhttp.onload = function() {
            if (this.readyState == 4 && this.status == 200) {
                // Parse JSON response to get the latitude and longitude of zip code
                var jsonResponse = JSON.parse(this.responseText);
                var results = jsonResponse.results;
                var userLocation = results[0].geometry.location;
                var userLatLng = new google.maps.LatLng(userLocation.lat, userLocation.lng);

                var itemResults = document.getElementsByClassName("grid-item");
                for (i = 0; i < itemResults.length; i++) {
                    // Get the latitude and longitude of each item in the search results
                    var itemLatClass = itemResults[i].getElementsByClassName("item-latitude");
                    var itemLatitude = itemLatClass[0].innerHTML;
                    var itemLongClass = itemResults[i].getElementsByClassName("item-longitude");
                    var itemLongitude = itemLongClass[0].innerHTML;

                    // Calculate distance between user's zip code and item location in miles
                    var itemLatLng = new google.maps.LatLng(itemLatitude, itemLongitude);
                    var metersDistance = google.maps.geometry.spherical.computeDistanceBetween(userLatLng, itemLatLng);
                    var milesDistance = metersDistance * 0.000621371;

                    var userMiles = parameters.get("miles");
                    if (milesDistance > userMiles) {
                        // Remove item from search results and update result count
                        itemResults[i].style.display = "none";

                        var resultCount = document.getElementsByClassName("resultCount");
                        resultCount[0].innerHTML -= 1;
                        var result = document.getElementsByClassName("result");

                        if (resultCount[0].innerHTML == 1) {
                           result[0].innerHTML = " Result";
                        }
                        else {
                            result[0].innerHTML = " Results";
                        }
                    }
                }
            }
        };
        xhttp.open('GET', url, true);
        xhttp.send();
    }
}

// Set the sorting option that is selected based on the sort URL parameter
function setSort() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var sort = parameters.get("sort");

    if (sort == "relevance") {
        document.getElementById("sort").selectedIndex = 0;
    }
    else if (sort == "priceasc") {
        document.getElementById("sort").selectedIndex = 1;
    }
    else if (sort == "pricedesc") {
        document.getElementById("sort").selectedIndex = 2;
    }
}

// Update item condition parameter in the URL and refresh page when user changes the item condition filter
function updateCondition() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var newChecked = document.getElementById("new").checked;
    var usedChecked = document.getElementById("used").checked;

    if (newChecked && usedChecked) {
        parameters.set('condition', 'any');
    }
    else if (!newChecked && !usedChecked) {
        parameters.set("condition", "all");
    }
    else if (newChecked) {
        parameters.set('condition', 'new');
    }
    else if (usedChecked) {
        parameters.set('condition', 'used');
    }

    window.history.replaceState({}, '', '/results?' + parameters);
    window.location.assign(window.location.href);
}

// Update miles and zip code parameters in the URL and refresh page when the user changes the distance filter
function updateDistance() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var miles = document.getElementById("miles").value;
    var zipCode = document.getElementById("zipCode").value;

    if (!parameters.has("miles") && !parameters.has("zipCode")) {
        parameters.append("miles", miles);
        parameters.append("zipCode", zipCode);
    }
    else {
        parameters.set("miles", miles);
        parameters.set("zipCode", zipCode);
    }

    window.history.replaceState({}, '', '/results?' + parameters);
    window.location.assign(window.location.href);
}

// Update item sorting parameter in the URL and refresh page when the user changes the sorting option
function updateSort(value) {
    var url = new URL(window.location.href);
    var params = new URLSearchParams(url.search);
    params.set("sort", value);

    window.history.replaceState({}, '', '/results?' + params);
    window.location.assign(window.location.href);
}