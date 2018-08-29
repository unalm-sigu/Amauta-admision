new Vue({
    el: '#main',
    data: {
        generando: false,
        encuestaURL: APP.url('academico/encuestaestudiantil/curso/list'),
        cfgVerDocentes: {
            id: 'modalVerDocentes',
            header: false,
            showaccept: false,
            cancelbtn: 'Cerrar'
        },
        docentesSecciones: []
    },
    mounted: function () {
        let vue = this;
        $global.$on("estado", function (encuestaDocente) {
            vue.estado(encuestaDocente);
        });
    },
    methods: {
        verDocentes(seccion) {
            let vue = this;
            vue.docentesSecciones = seccion.docenteSeccion;
            vue.$refs.modalVerDocentes.open();
        },
        getDia(fecha) {
            if (fecha == "")
                return "";
            return fecha.split(" ")[0];
        },
        getHora(fecha) {
            if (fecha == "")
                return "";
            var time = fecha.split(" ")[1].split(":");
            var aamm = (parseInt(time[0]) > 11) ? "pm" : "am";
            var hh = (parseInt(time[0]) > 12) ? (parseInt(time[0]) - 12) : parseInt(time[0]);
            return (hh < 10 ? "0" : "") + hh + ":" + time[1] + " " + aamm;
        },
        generarEncuesta: function () {
            let vue = this;
            vue.generando = true;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/generar'),
                async: false,
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.generando = false;
                }, error: function () {
                    vue.generando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        estado: function (encuestaCurso) {
            let vue = this;
            swal({
                text: "¿Está seguro que desea cambiar el estado a la encuesta del curso?",
                icon: "warning",
                type: "warning",
                dangerMode: true,
                showCancelButton: true,
                closeOnConfirm: false,
                buttons: {
                    cancel: "No",
                    confirm: "Si, estoy seguro"
                }
            }).then((willDelete) => {
                if (willDelete) {
                    vue.changeEstado(encuestaCurso);
                }
            });
        },
        changeEstado: function (encuestaDocente) {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/estado'),
                async: false,
                data: {'id': encuestaDocente.id},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.generando = false;
                }, error: function () {
                    vue.generando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});
