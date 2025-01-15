<template>
    <modal-vik ref="modalDeshabilitar"
               v-bind="modalDeshabilitar"
               v-bind:okaction="saveDeshabilitar">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}} {{ciclo.descripcion}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="form-group">
                        <label>Alumno</label>
                        <span class="item-form-control item-form-gray text-primary">
                            {{alumnoNiv.alumno.persona.apellidosNombres}}
                        </span>
                    </div>

                    <div class="form-group">
                        <label>{{tipoCarrera}}</label>
                        <span class="item-form-control item-form-gray text-primary">
                            {{alumnoNiv.alumno.carrera.nombre}}
                            <template v-if="verFacultad">
                                - {{alumnoNiv.alumno.carrera.facultad.nombre}}
                            </template>
                        </span>
                    </div>

                    <div class="form-group">
                        <label>Motivo por el cual deshabilita al alumno</label>
                        <textarea v-model="alumnoNiv.motivo" class="form-control" rows="3" required="yes"></textarea>
                    </div>
                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                alumnoNiv: null,
                raptor: null,
                visible: false,
                verFacultad: false,
                ciclo: JSON.parse(cicloJson),
                tipoCarrera: "id-form-deshabilitar-matricula",
                form: "id-form-deshabilitar-matricula",
                title: "Deshabilitar inscripción del",
                modalDeshabilitar: VUE_MODAL.structFormAjax({
                    id: "id-modal-deshabilitar-matricula",
                    okbtn: "Deshabilitar alumno",
                    okclass: "btn-danger"
                })
            };
        },
        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.alumnoNiv = JSON.parse(JSON.stringify(item));

                this.verFacultad = true;
                this.tipoCarrera = "Especidalidad";
                let carrera = this.alumnoNiv.alumno.carrera.nombre;
                let facultad = this.alumnoNiv.alumno.carrera.facultad.nombre;
                if (carrera === facultad) {
                    this.tipoCarrera = "Facultad";
                    this.verFacultad = false;
                }

                this.visible = true;
                this.$refs.modalDeshabilitar.open();
            },
            saveDeshabilitar() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                let payload = {
                    id: this.alumnoNiv.id,
                    motivo: this.alumnoNiv.motivo
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/deshabilitarAlumno`,
                    modal: this.$refs.modalDeshabilitar,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalDeshabilitar;
            },

            // metodos genericos
            getListIds(list) {
                return list.map(item => item.id).join(',');
            },
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>