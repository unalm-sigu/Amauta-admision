
new Vue({
    el: '#configuracion',
    data: {
        eventos: [{id: 1, value: 'Matricula Regular'},
            {id: 2, value: 'Matricula verano'},
            {id: 3, value: 'Reinscripción'}],
        tipos: [{id: 1, value: 'Por barrido'},
            {id: 2, value: 'En línea'}],
        tabs: [{id: 1, value: 'Barrido'}, {id: 1, value: 'En línea'}],
//        config: {
//            evento: 1,
//            tipo: 2,
//            turnoDia: 4,
//            duracion: 40,
//            espera: 20,
//            alumnos: 320,
//            fechaInicio: '12/08/2012',
//            fechaFin: '12/08/2012'
//        }
        config: {},
        dias: [{id: 1, dia: "07/02"},
            {id: 2, dia: "08/02"},
            {id: 3, dia: "09/02"},
            {id: 4, dia: "10/02"}],
        horas: [{id: 1, hora: "8:30",
                turnos: [
                    {id: 1, cant: "320"},
                    {id: 2, cant: "9:00"},
                    {id: 3, cant: "10:00"},
                    {id: 4, cant: "10:30"}]},
            {id: 2, hora: "9:00",
                turnos: [
                    {id: 5, cant: "320"},
                    {id: 6, cant: "320"},
                    {id: 7, cant: "10:00"},
                    {id: 8, cant: "10:30"}]},
            {id: 3, hora: "10:00",
                turnos: [
                    {id: 9, cant: "320"},
                    {id: 10, cant: "320"},
                    {id: 11, cant: "10:00"},
                    {id: 12, cant: "10:30"}]},
            {id: 4, hora: "10:30",
                turnos: [
                    {id: 13, cant: "8:30"},
                    {id: 14, cant: "9:00"},
                    {id: 15, cant: "10:00"},
                    {id: 16, cant: "10:30"}]}]

    },
    created() {
        let $vue = this;
        $(function () {

            $(document).ready(function () {
//toggle `popup` / `inline` mode
                $.fn.editable.defaults.mode = 'popup';
                //make status editable
                $('#status').editable({
                    type: 'select',
                    title: 'Select status',
                    placement: 'right',
                    value: 2,
                    source: [
                        {value: 1, text: 'status 1'},
                        {value: 2, text: 'status 2'},
                        {value: 3, text: 'status 3'}
                    ]
                            /*
                             //uncomment these lines to send data on server
                             ,pk: 1
                             ,url: '/post'
                             */
                });

                console.log($vue.horas);
                $vue.horas.forEach(function (elem) {
                    elem.turnos.forEach(function (turno) {
                        $('#' + turno.id).editable();
                    });
                });
                $('#1').editable();
                $('#datepicker1').datepicker();
                $('#datepicker2').datepicker();
                $('#datetimepicker1').timepicker();
                $('#datetimepicker2').timepicker();
                $('#datetimepicker3').timepicker();
                $('#date1').datepicker();
                $('#date1').datepicker();
            });
        });



    },
    mounted() {

        $('.numeric').numeric({negative: false});
        let $vue = this;
        $('#datepicker1').datepicker().on(
                'changeDate', () => {
            $vue.config.fechaInicio = $('#date1').val();
            console.log($vue.config.fechaInicio);
        }
        );
        $('#datepicker2').datepicker().on(
                'changeDate', () => {
            $vue.config.fechaFin = $('#date2').val();
            console.log($vue.config.fechaFin);
        }
        );

    },
    methods: {
        nuevo() {

            $("#myModal").modal('show');
        },
        save() {
            let $vue = this;
            $vue.config.espera =  parseInt($vue.config.espera);
            $vue.config.duracion =  parseInt($vue.config.duracion);
            $vue.config.alumnos =  parseInt($vue.config.alumnos);
//            $('#date1').val((new Date($vue.config.fechaFin)).toLocaleDateString('la', {year: 'numeric', month: 'numeric', day: 'numeric'}));
            console.log($('#date1').val());
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matricula/saveConfiguracion'),
                contentType: "application/json",
                data: JSON.stringify($vue.config),
                success: function (response) {
                   
                }
            });

            $("#myModal").modal('hide');
        },
        carga() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    $vue.alumnoCurso = response.data;
                    var i = 1;
                    $vue.alumnoCurso.forEach(function (element) {
                        var obj = {id: 1, value: element.descripción};
                        $vue.listCiclos.push(obj);
                        i++;
                    })
                    console.log($vue.listCiclos);
                }
            });
        },
    }

})
        