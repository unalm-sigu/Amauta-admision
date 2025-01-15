<template>
    <modal-vik ref="modalAddAlumno"
               v-bind="modalAddAlumno"
               v-bind:okaction="saveAddAlumno">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}} {{ciclo.descripcion}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="form-group">
                        <label>Alumno</label>

                        <multiselect v-model="alumnoNiv.alumno"
                                     v-bind:options="alumnos"
                                     v-bind:allow-empty="false"
                                     v-on:search-change="searchAlumno"
                                     v-on:input="selectAlumno"
                                     track-by="id"
                                     placeholder="Seleccione un alumno"
                                     v-bind:internal-search="false"
                                     v-bind:showNoOptions="true"
                                     v-bind:show-labels="false">

                            <template slot="singleLabel" slot-scope="props">
                                <span class="text-primary h4">{{ props.option.persona.nombreCompleto }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="block bold">{{ props.option.persona.nombreCompleto }} </span>
                                <span class="block text-xs">{{ props.option.codigo }} - {{ props.option.carrera.nombre }}</span>
                                <span class="text-xs">{{ props.option.persona.tipoDocumento.simbolo }} - {{ props.option.persona.numeroDocIdentidad }}</span>
                            </template>

                            <template slot="noOptions">Lista vacía</template>
                            <template slot="noResult">Sin resultados</template>

                        </multiselect>

                        <input v-bind:value="getObjectId(alumnoNiv.alumno)" required="true" type="text" class="hide"/>

                    </div>

                    <template v-if="alumnoNiv.alumno">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Matrícula</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{alumnoNiv.alumno.codigo}}
                                    </span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>{{alumnoNiv.alumno.persona.tipoDocumento.nombre}}</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{alumnoNiv.alumno.persona.numeroDocIdentidad}}
                                    </span>
                                </div>
                            </div>
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
                            <label>Modalidad ingreso</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{alumnoNiv.alumno.postulantePregrado.modalidadIngreso.nombre}}
                            </span>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Ciclo ingreso</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{alumnoNiv.alumno.cicloIngreso.descripcion}}
                                    </span>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Ciclo examen</label>
                                    <span class="item-form-control item-form-gray text-primary">
                                        {{alumnoNiv.alumno.postulantePregrado.cicloPostula.cicloAcademico.descripcion}}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </template>
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
                alumnos: [],
                visible: false,
                verFacultad: false,
                tipoCarrera: "Especialidad",
                ciclo: JSON.parse(cicloJson),
                form: "id-form-add-alumno",
                title: "Agregar alumno al proceso de nivelación ",
                modalAddAlumno: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-alumno",
                    okbtn: "Agregar alumno",
                    okclass: "btn-primary"
                })
            };
        },
        methods: {
            open(raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.alumnoNiv = {alumno: null};

                this.verFacultad = true;
                this.tipoCarrera = "Especidalidad";
                this.visible = true;
                this.$refs.modalAddAlumno.open();
            },
            searchAlumno(nombre) {
                if (nombre) {
                    myUtils.axios(VUE_AXIOS.structGetData({
                        url: `/${rutaModulo}/searchAlumno?nombre=${nombre}`
                    })).then((resp) => this.alumnos = resp.data.data);
                }
            },
            selectAlumno(item) {
                let carrera = item.carrera.nombre;
                let facultad = item.carrera.facultad.nombre;
                if (carrera === facultad) {
                    this.tipoCarrera = "Facultad";
                    this.verFacultad = false;
                }
            },
            saveAddAlumno() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                let payload = {
                    id: this.alumnoNiv.alumno.id
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/addAlumno`,
                    modal: this.$refs.modalAddAlumno,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalAddAlumno;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>