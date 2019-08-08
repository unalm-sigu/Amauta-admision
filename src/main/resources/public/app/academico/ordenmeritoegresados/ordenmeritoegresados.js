Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ordenmeritoVUE',
    data: {
        cicloAcademico: {},
        alumno: {carrera: {facultad: {}}},
        egresado: {alumno: {}},
        cicloSelected: {},
        listAlumno: [],
        URL: APP.url('academico/ordenmeritoegresados'),
        modalAgregarAlumno: {
            id: 'modalAgregarAlumno',
            header: true,
            title: 'Agregar alumno egresado',
            okbtn: 'Guardar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true,
            modalsize: 'modal-medium',
            form: "formAddAlm"
        },
    },
    mounted() {
        $("#cicloChange").select2();
    },
    methods: {
        generarDatos() {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/generardatos`, this.cicloAcademico)
                    .then(response => {
                        this.$refs.raptor.loadRemoteData();
                        MODAL.hideWait();
                    })
        },
        calcularMeritos() {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/calcularmeritos`, this.cicloAcademico)
                    .then(response => {
                        this.$refs.raptor.loadRemoteData();
                        MODAL.hideWait();
                    })
        },
        nombreforShow(alumno) {
            if (!alumno || !alumno.id) {
                return;
            }
            return alumno.persona.nombreCompleto + " - " + alumno.codigo;
        },
        openModal(param) {
            let $vue = this;

            if (param === "modalAgregarAlumno") {
                $("#" + $vue.modalAgregarAlumno.form).parsley().destroy();
                $vue.alumno = {carrera: {facultad: {}}};
                $vue.modalAgregarAlumno.title = " Agregar alumno egresado";
                $vue.modalAgregarAlumno.okbtn = "Guardar";
                $vue.$refs.modalAgregarAlumno.open();
            }

            var data = $('#cicloChange').select2('data');
            $vue.cicloSelected = {id: data.id, descripcion: data.text};
        },
        searchAlumno(parametro) { // like nombre
            let $vue = this;
            if (parametro === '') {
                return;
            }
            $vue.listAlumno = [];
            const params = new URLSearchParams();
            params.append('parametro', parametro);
//            params.append('idCiclo', $("#cicloChange").val());
            axios.post(`${this.URL}/allAlumnoLikeNombres`, params)
                    .then(function (response) {
                        if (response.data.success) {
                            $vue.listAlumno = response.data.data;
                        } else {
                            notify(response.data.message, "warning");
                        }
                    }).catch(function (error) {
                notify(error.errorComunicacion, "error");
            });
        },
        saveEgresado() {
            let $vue = this;

            $vue.egresado.alumno = $vue.alumno;
            $vue.egresado.cicloAcademico = $vue.cicloSelected;

            if ($("#" + $vue.modalAgregarAlumno.form).parsley().validate() !== true) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }
            AXIOS.post(`${this.URL}/saveEgresado`, $vue.egresado)
                    .then(response => {
                        $vue.$refs.modalAgregarAlumno.close();
                    });
        }
    }
});


$(function () {
    $("body").delegate("#cicloChange", "change", function (e) {
        $.ajax({
            url: APP.url('academico/ordenmerito/changeciclo'),
            type: 'POST',
            async: false,
            data: {ciclo: $("#cicloChange").val()}
        }).done(function (html) {
            location.reload();
        });
    });
});




