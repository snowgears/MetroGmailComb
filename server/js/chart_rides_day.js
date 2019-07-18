var chart = c3.generate({
    bindto: '#day_chart',
    data: {
        x : 'x',
        url: '/json/metro_rides_daily.json',
        mimeType: 'json',
        type: 'bar'
    },
    axis: {
        x: {
            type: 'category' // this needed to load string x value
        }
    }
});