/**
 * Created by cwaadd on 26/09/2017.
 */

function loadData(graphData){

    var data = null;

    if(graphData !== null) {

        var data = [];

        for (var i = 0; i < graphData.length; i++) {
            var point = {
                t: graphData[i].date,
                y: graphData[i].price
            };
            data.push(point);
        }
    }
    return data;
}