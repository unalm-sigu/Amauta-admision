Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#convalTrasladoExterno',
    data: {
        alumno: JSON.parse(alumnoJson),
        ciclo: JSON.parse(cicloJson),
        listTramiteTraslado: JSON.parse(listTramiteTrasladoJson),
        listAlumnoCursoCurricula: JSON.parse(listAlumnoCursoCurriculaJson),
        listCursoConvalidado: JSON.parse(listCursoConvalidadoJson),
        listAlumnoCursoCOptions: [],
        curso: null,
        cursos: [],
        tramiteTrasladoActivo: {tipoTraslado: null, id: null},
        total: 0
    },
    created: function () {
        let $vue = this;
        $vue.updateListOptions();
        $vue.countTotal();
        $vue.findTramiteTrasladoActivo();
    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        customLabel(item) {
            return item.curso.nombre + " - " + item.curso.codigo + " Nro Ciclo " + item.numeroCiclo;
        },
        returnEstado(estado) {
            return estado === 'ACT' ? 'Activo' : 'Inactivo';
        },
        countTotal() {
            let $vue = this;
            for (var i = 0; i < $vue.listCursoConvalidado.length; i++) {
                $vue.updateTotalCreditos($vue.listCursoConvalidado[i], "add");
            }
        },
        returnTipoCurso(tipo) {

            if (tipo === 'TEO') {
                return "Teoría"
            }
            if (tipo === 'TEOPRA') {
                return "Teoría y Práctica"

            }
            if (tipo === 'PRA') {
                return "Práctica"

            }
        },
        loadCursos(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/alumno/allCurso"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.cursos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        listUpdate(item) {
            let $vue = this;
            for (var i = 0; i < $vue.listAlumnoCursoCOptions.length; i++) {
                if ($vue.listAlumnoCursoCOptions[i].id === item.id) {
                    $vue.listAlumnoCursoCOptions.splice(i, 1);
                }
            }
        }
        ,
        updateListOptions() {
            let $vue = this;
            $vue.listAlumnoCursoCOptions = [];
            $vue.listAlumnoCursoCOptions = Object.assign([], $vue.listAlumnoCursoCurricula);
            for (var j = 0; j < $vue.listCursoConvalidado.length; j++) { // cursos seleccionados
                for (var i = 0; i < $vue.listAlumnoCursoCOptions.length; i++) {
                    if ($vue.listAlumnoCursoCOptions[i].curso.id === $vue.listCursoConvalidado[j].curso.id) {
                        $vue.listAlumnoCursoCOptions.splice(i, 1); //optiones a no mostar en el multiselect
                    }
                }
            }
        },
        addCurso() {
            let $vue = this;
            if ($vue.curso === null) {
                notify("Debe seleccionar un curso para agregar.", "warning")
                return;
            }
            let objectClone = Object.assign({}, $vue.curso);
            $vue.listUpdate(objectClone);
            $vue.listCursoConvalidado.push({id: null, curso: objectClone, tramiteTraslado: {id: $vue.tramiteTrasladoActivo.id, alumno: $vue.alumno}});
            $vue.updateTotalCreditos($vue.curso, "add");
            $vue.curso = null;
        },
        deleteItem(index, item) {
            let $vue = this;
            $vue.updateTotalCreditos(item.curso, "remove");
            $vue.listCursoConvalidado.splice(index, 1);
            $vue.updateListOptions();

        },
        updateTotalCreditos(item, param) {
            let $vue = this;
            if (param === "add") {
                if (item.curso != null) {
                    $vue.total = $vue.total + (item.curso.creditos);
                } else {
                    $vue.total = $vue.total + (item.creditos);
                }
            }

            if (param === "remove") {
                if (item.curso != null) {
                    $vue.total = $vue.total + (item.curso.creditos);
                } else {
                    $vue.total = $vue.total + (item.creditos);
                }
            }
        },
        findTramiteTrasladoActivo() {
            let $vue = this;
            for (var i = 0; i < $vue.listTramiteTraslado.length; i++) {
                if ($vue.listTramiteTraslado[i].estado === 'ACT') {
                    $vue.tramiteTrasladoActivo = $vue.listTramiteTraslado[i];
                }
            }
        },
        desactivarTraslados() {
            let $vue = this;
            for (var i = 0; i < $vue.listTramiteTraslado.length; i++) {
                if ($vue.listTramiteTraslado[i].estado === 'ACT') {
                    $vue.listTramiteTraslado[i].estado = 'INA';
                }
            }
        },
        save() {
            let $vue = this;
            let list = [];

            for (var i = 0; i < $vue.listCursoConvalidado.length; i++) {
                if ($vue.listCursoConvalidado[i].id === null) {
                    list.push($vue.listCursoConvalidado[i]);
                }
            }

            if (list.length === 0) {
                notify("Debe agregar almenos un curso para convalidar.", "warning");
                return;
            }

            if ($vue.tramiteTrasladoActivo.tipoTraslado === 'INTES') {
                var form = $("#formTraslado");
                if (!form.parsley().validate()) {
                    notify("Debe completar todos los campos requeridos", "error");
                    return;
                }
            }

            let trasladoBean = {listCursoConvalidado: list, total: $vue.total, alumno: $vue.alumno, tramiteTraslado: Object.assign({}, $vue.tramiteTrasladoActivo)};
            let texto = (list.length > 1 ? 'los ' + list.length + ' cursos seleccionados?' : 'el curso seleccionado?');
            let txtAdvertencia = " <b>Sí acepta, ya no podrá convalidar otros cursos hasta una nueva resolución.</b>";
            bootbox.confirm({
                message: '¿Está seguro que desea convalidar ' + texto + txtAdvertencia,
                buttons: {
                    confirm: {label: 'Sí, aceptar', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        axios.post("/" + rutaModulo + "/saveListCursoConvalidado", trasladoBean)
                                .then(response => {
                                    if (response.data.success) {
                                        notify(response.data.message, "success");
                                        $vue.listCursoConvalidado = response.data.data;
                                        $vue.updateListOptions();
                                        $vue.countTotal();
                                        $vue.findTramiteTrasladoActivo();
                                        $vue.tramiteTrasladoActivo = {tipoTraslado: null, id: null};
                                        $vue.desactivarTraslados();

                                    } else {
                                        notify(response.data.message, "warning");
                                    }
                                }).catch(e => {
                            notify(MESSAGES.errorComunicacion, "error");
                        });

                    }
                }
            });


        }
    }
});
