Vue.component("multiselect", window.VueMultiselect.default);
const VueFilePicker = use('/_vue/modules/VueFilePicker.vue');
const ModalSimple = use('/_vue/modules/ModalSimple.vue');
const RaptorTable = use('/_vue/modules/RaptorTable.vue');
new Vue({
    el: '#main',
    mixins: [VueLoader],
    components: {
        VueFilePicker,ModalSimple,RaptorTable
    },
    data: {
        silaboURL: APP.url('academico/silabo/list'),
        silaboCurso: null,
        filtroDepartamento: null,
        cursos: [],
        departamentos: [],
        seleccionados: [],
        files: [],
        ciclos: []
    },
    mounted: function () {
        let $vue = this;
        $vue.allCiclo();
        $vue.allDepartamento();
    },
    methods: {
        save() {
            let $vue = this;
            axios_.post('/academico/silabo/save', $vue.silaboCurso)
                    .then(response => {
                        notify(response.data, 'info');
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalSilabo.close();
                    }, () => $vue.$refs.modalSilabo.stop());
        },
        openModalSilabo() {
            let $vue = this;
            $vue.silaboCurso = {};
            $vue.$refs.modalSilabo.open();
        },
        findCurso(nombre) {
            let $vue = this;
            if (nombre) {
                $vue.cursos = [];
            }
            axios_.get('/academico/silabo/allCursoMod', {params: {nombre: nombre}})
                    .then(response => {
                        $vue.cursos = response.data;
                    }, () => null)
        },
        findDepartamento(nombre) {
            let $vue = this;
            if (nombre) {
                $vue.departamentos = [];
            }
            axios_.get('/academico/silabo/allDepartamentoMod', {params: {nombre: nombre}})
                    .then(response => {
                        $vue.departamentos = response.data;
                    }, () => null);
        },
        editar(item) {
            let $vue = this;
            $vue.silaboCurso = {...item};
            $vue.$refs.modalSilabo.open();
        },
        eliminar(item) {
            let $vue = this;
            swal('¿Seguro que desea eliminar el silabu?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Sí, Eliminar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                axios_.post('/academico/silabo/delete', {id: item.id})
                        .then(response => {
                            notify(response.data, 'info');
                            $vue.$refs.load.loadRemoteData();
                            return swal({text: response.data, icon: "success", button: false, timer: 1000});
                        }, () => {
                            return swal(APP.errorComunicacion, "error");
                        });

            }).catch(err => {
                if (err) {
                    swal(APP.errorComunicacion, "error");
                } else {
                    swal.stopLoading();
                    swal.close();
                }
            });

        },
        revision(item, st) {
            let $vue = this;
            axios_.post('/academico/silabo/revision', {id: item.id, estado: st})
                    .then(response => {
                        notify(response.data, 'info');
                        $vue.$refs.load.loadRemoteData();
                    }, () => null);
        },
        async allCiclo() {
            let $vue = this;
            const response = await axios_.get('/academico/silabo/allCiclo');
            $vue.ciclos = response.data;
        },
        async allDepartamento() {
            let $vue = this;
            const response = await axios_.get('/academico/silabo/allDepartamento');
            $vue.departamentos = response.data;
        },
        onFileUplad(el) {
            let $vue = this;
            const archivo = el.target.files[0];
            const nombre = archivo.name;
            if (!/\.(jpg|png|jpeg|webp|pdf|doc|docx)$/i.test(nombre)) {
                notify('¡Este tipo de archivo no esta permitido!', 'error');
                return;
            }
            let formData = new FormData();
            formData.append('file', archivo);
            $vue.showLoader();
            axios_.post("/comun/archivo/upload/", formData)
                    .then(({data}) => {
                        $vue.silaboCurso.rutaDocumento = data.data.nombre;
                        $vue.silaboCurso.fileUpdated = 1;
                        $vue.hideLoader();
                        $vue.$forceUpdate();
                    }, err => $vue.hideLoader());
        },
        descargarSeleccionados() {
            let $vue = this;
            if (!$vue.seleccionados.length) {
                notify('¡No a seleccionado ningun silabus!', 'error');
                return;
            }
            $vue.showLoader("Se están descargando " + $vue.seleccionados.length + " sílabos");
            axios_blob.get(APP.url('academico/silabo/descargar'),
                    {params: {silabus: $vue.seleccionados.join(",")}})
                    .then(response => {
                        UTIL_BLOB_INLINE.save(response);
                        $vue.hideLoader()
                    }, () => {
                        $vue.hideLoader()
                    });
        },
        changeSelect(idSilabus) {
            let $vue = this;
            if ($vue.seleccionados.indexOf(idSilabus) < 0) {
                $vue.seleccionados.push(idSilabus);
                $vue.$forceUpdate();
                return;
            }
            $vue.seleccionados.splice($vue.seleccionados.indexOf(idSilabus), 1);
            $vue.$forceUpdate();
        },
        estaSeleccionado(idSilabus) {
            let $vue = this;
            return $vue.seleccionados.indexOf(idSilabus) >= 0;
        },
        changeFilterDepartamento() {
            let $vue = this;
            $vue.$refs.load.querie.push({name: 'departamento', value: $vue.filtroDepartamento ? $vue.filtroDepartamento.id : null});
            $vue.$refs.load.loadRemoteData();
        },
        descargarReporte() {
            let $vue = this;
            $vue.showLoader();
            axios_blob.get(APP.url('academico/silabo/reporte'))
                    .then(response => {
                        UTIL_BLOB.save(response);
                        $vue.hideLoader();
                    }, () => $vue.hideLoader());
        },
        openFile(item) {
            let $vue = this;
            if (!/\.(doc|docx)$/i.test(item.rutaDocumento)) {
                window.open(item.rutaDocumento, '_blank');
                return;
            }
            window.open("https://docs.google.com/gview?url=" + item.rutaDocumento + "&embedded=true", '_blank');
        }
    }
});
