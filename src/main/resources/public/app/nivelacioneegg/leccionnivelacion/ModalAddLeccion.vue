<template>
    <modal-vik ref="modalAddLeccion"
               v-bind="modalAddLeccion"
               v-bind:okaction="saveLeccion">
        <div slot="body">

            <h3 class="block m-b-xs bold text-primary">
                Crear lección - {{seccion.cursoCiclo.curso.nombre}}
            </h3>
            <h5 class="block m-t-xs bold text-primary m-b-lg">
                Sección - {{seccion.codigo}}
            </h5>

            <form v-bind:id="form" data-parsley-validate="">

                <template v-if="visualizar">
                    <div class="form-group">
                        <label>Fecha</label>

                        <multiselect
                            v-model="leccion.fechaEnum"
                            v-bind:options="fechas"
                            v-bind:allow-empty="false"
                            v-on:input="selectFecha"
                            track-by="fecha"
                            placeholder="Seleccione un curso"
                            v-bind:internal-search="false"
                            v-bind:showNoOptions="true"
                            v-bind:show-labels="false">
                            <template slot="singleLabel" slot-scope="props">
                                <span class="text-primary h5 bold">{{ props.option.fecha }}</span>
                            </template>

                            <template slot="option" slot-scope="props">
                                <span class="block">
                                    {{ props.option.fecha }} -
                                    {{ props.option.estadoEnum.value }}
                                </span>
                            </template>

                            <template slot="noOptions">Lista vacía</template>
                            <template slot="noResult">Sin resultados</template>
                        </multiselect>
                        <input type="text" class="hide" v-model="leccion.fecha" required="true"/>         
                    </div>

                    <div class="form-group">
                        <label>Tema de la lección</label>
                        <textarea v-model="leccion.temaClase" class="form-control" rows="3" required="yes"></textarea>
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
                title: "",
                form: "id-form-add-leccion",
                raptor: null,
                leccion: null,
                fechas: [],
                visualizar: false,
                ciclo: JSON.parse(cicloJson),
                seccion: JSON.parse(seccionJson),
                modalAddLeccion: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-leccion",
                    okbtn: "Guardar",
                    okclass: "btn-success"
                })
            };
        },

        created() {},
        mounted() {},

        methods: {
            open(raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.leccion = {
                    fecha: null,
                    fechaEnum: null,
                    cursoNivelacion: {id: this.seccion.id}
                };

                this.fechas = [];
                this.loadFechas();

                this.$refs.modalAddLeccion.open();
                this.raptor = raptor;
                this.visualizar = true;
            },
            loadFechas() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/${this.seccion.id}/fechasLecciones`
                })).then((resp) => (this.fechas = resp.data.data));
            },
            selectFecha(item) {
                this.leccion.fecha = item.fecha;
            },

            saveLeccion() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                let payload = JSON.parse(JSON.stringify(this.leccion));
                delete payload.fechaEnum;
                payload.modoPrueba = 1;

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/crearLeccion`,
                    modal: this.$refs.modalAddLeccion,
                    raptor: this.raptor,
                    body: payload
                })).then((resp) => {
                    const leccion = resp.data.data;
                    const url = APP.url(`${rutaModulo}/${leccion.id}/asistencia${myUtils.getOrigenURL()}`);
                    location.href = url;
                });
            },
        },
    };
</script>
