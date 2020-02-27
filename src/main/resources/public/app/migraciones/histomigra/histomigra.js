new Vue({
    el: '#histoVUE',
    data: {
        histoURL: APP.url(rutaModulo + "/list/" + JSON.parse(alumnoJson).id),
        pagination: {'total-items': 0, 'items-per-page': 500, 'max-size': 3, 'boundary-link-numbers': true},
        alumno: JSON.parse(alumnoJson),
        historias: []
    },
    mounted: function () {
        let $vue = this;
        //$vue.loadData();
    },
    methods: {
        loadData() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + "/list/" + $vue.alumno.id),
                success(response) {
                    if (response.success) {
                        $vue.historias = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        migrarItem(item) {
            let $vue = this;
            if (!item.algoFalta) {
                notify("Este registro ya fue migrado", "error");
                return;
            }
            if (!item.algoFalta) {
                notify("Este registro ya fue migrado", "error");
                return;
            }
            if (item.migrando) {
                notify("Este registro ya esta siendo migrado", "error");
                return;
            }
            item.migrando = true;

            axios.post(`/${rutaModulo}/migrarCurso`, item)
                    .then(response => {
                        if (response.data.success) {
                            let histo = response.data.data;
                            item.estadoCiclo = histo.estadoCiclo;
                            item.aluciclocurso = histo.aluciclocurso;
                            item.creditosOk = histo.creditosOk;
                            item.notaOk = histo.notaOk;
                            item.movOk = histo.movOk;
                            item.algoFalta = histo.algoFalta;
                            item.migrando = histo.migrando;

                        } else {
                            notify(response.data.message, "warning");
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            });

        }
    }
});

