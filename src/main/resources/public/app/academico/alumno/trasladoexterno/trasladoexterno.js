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
        tramiteTrasladoActivo: {},
        total: 0
    },
    created: function () {
        let $vue = this;
        $vue.updateListOptions();
        $vue.countTotal();
//        $vue.findTramiteTrasladoActivo();
    },
    mounted: function () {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    },
    methods: {
        customLabel(item) {
            return item.curso.nombre + " - " + item.curso.codigo + " Nro Ciclo " + item.numeroCiclo;
        },
        customLabelRes( { resolucion}) {
            if (resolucion == null) {
                return ""
            }
            return `${resolucion.numero} – ${resolucion.serie}`;
        },
        returnEstado(estado)
        {
            return estado === 'ACT' ? 'Activo' : 'Inactivo';
        },
        countTotal() {
            let $vue = this;
            $vue.total = 0;
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
            $vue.listCursoConvalidado.push({id: null, curso: objectClone, creditos: objectClone.creditos, tramiteTraslado: {alumno: $vue.alumno}});
            $vue.updateTotalCreditos($vue.curso, "add");
            $vue.curso = null;
        },
        deleteItem(index, item) {
            let $vue = this;
            $vue.updateTotalCreditos(item.curso, "remove");
            $vue.listCursoConvalidado.splice(index, 1);
            $vue.updateListOptions();

        },
        valid(item) {
            let $vue = this;
            var ret = false;
            if (item.tramiteTraslado.tipoTraslado == 'TRAS') {
                ret = true;
            }
            if ($vue.tramiteTrasladoActivo.id != null && $vue.tramiteTrasladoActivo.tipoTraslado == 'TRAS') {
                if (item.tramiteTraslado.tipoTraslado == null || item.tramiteTraslado.tipoTraslado == 'TRAS') {
                    ret = true;
                }
            }
            return ret;
        },
        validIntes(item) {
            let $vue = this;
            var ret = false;
            
            if ($vue.tramiteTrasladoActivo.id != null && $vue.tramiteTrasladoActivo.tipoTraslado == 'INTES') {
                if (item.tramiteTraslado.tipoTraslado == null || item.tramiteTraslado.tipoTraslado == 'INTES') {
                    ret = true;
                }
            }
            return ret;
        },
        updateTotalCreditos(item, param) {
            let $vue = this;
            if (param === "add") {
                $vue.total = $vue.total + (Number(item.creditos));
            }

            if (param === "remove") {
                $vue.total = $vue.total - (Number(item.creditos));
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
            let totalNuevos = 0;
            let mapId = new Map();
            let nombre_curso = "";
            let repetido = false;
            for (var i = 0; i < $vue.listCursoConvalidado.length; i++) {
                list.push($vue.listCursoConvalidado[i]);
                if ($vue.listCursoConvalidado[i].id === null && $vue.tramiteTrasladoActivo.tipoTraslado == 'TRAS') {
                    totalNuevos += $vue.listCursoConvalidado[i].curso.creditos;
                }
                if (mapId.get($vue.listCursoConvalidado[i].curso.id) != null) {
                    nombre_curso = mapId.get($vue.listCursoConvalidado[i].curso.id);
                    repetido = true;
                    break;
                }
                mapId.set($vue.listCursoConvalidado[i].curso.id, $vue.listCursoConvalidado[i].curso.nombre);
            }
            if (repetido) {
                notify("Está repitiendo el curso " + nombre_curso, "warning");
                return;
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

            let trasladoBean = {listCursoConvalidado: list, total: totalNuevos, alumno: $vue.alumno, tramiteTraslado: Object.assign({}, $vue.tramiteTrasladoActivo)};
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

                        MODAL.showWait("Espere un momento por favor");
                        axios.post("/" + rutaModulo + "/saveListCursoConvalidado", trasladoBean)
                                .then(response => {
                                    if (response.data.success) {
                                        notify(response.data.message, "success");
                                        $vue.listCursoConvalidado = response.data.data;
                                        $vue.updateListOptions();
                                        $vue.countTotal();
//                                        $vue.findTramiteTrasladoActivo();
                                        $vue.tramiteTrasladoActivo = {tipoTraslado: null, id: null};
                                        $vue.desactivarTraslados();
                                        MODAL.hideWait();
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
