 <modal-vik ref="modalNuevoBecados" v-bind="modalNuevoBecados">
    <div slot="body">

      <h4 class="text-primary block">testsss</h4>

<!--      <form v-bind:id="form">-->
<!--        <template v-if='visible'>-->
<!--          <div class="form-group m-t-lg">-->
<!--            <label>Objetivos de la cita</label>-->
<!--            <multiselect-->
<!--                v-model="citaNueva.planesTutoriales"-->
<!--                v-bind:options='objetivos'-->
<!--                v-bind:multiple='true'-->
<!--                v-bind:close-on-select='false'-->
<!--                v-bind:multiple='true'-->
<!--                label='objetivo'-->
<!--                track-by='id'-->
<!--                deselect-label=" "-->
<!--                select-label=" "-->
<!--                placeholder=" "-->
<!--                v-bind:allow-empty="false">-->

<!--              <template slot="noOptions">No existe objetivos disponibles</template>-->
<!--              <template slot="noResult">No hay objetivos seleccionados</template>-->
<!--            </multiselect>-->

<!--            <input type="text" class="hide" required="true"  v-bind:value="getListIds(citaNueva.planesTutoriales)"  />-->
<!--          </div>-->

<!--          <div class="form-group">-->
<!--            <label>Asunto</label>-->
<!--            <textarea v-model="citaNueva.asunto" class="form-control" rows="3" required="yes"></textarea>-->
<!--          </div>-->

<!--          <div class="row">-->
<!--            <div class="col-md-6">-->
<!--              <div class="form-group">-->
<!--                <label>Fecha cita</label>-->
<!--                <div class="input-group date">-->
<!--                  <date-picker v-model="citaNueva.fecha"-->
<!--                               required="true"-->
<!--                               v-bind:config="configDate"-->
<!--                               v-bind:wrap="true" >-->
<!--                  </date-picker>-->
<!--                  <div class="input-group-addon">-->
<!--                    <span class="fa fa-calendar"></span>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->

<!--            <div class="col-md-6">-->
<!--              <div class="form-group">-->
<!--                <label>Hora</label>-->
<!--                <date-picker v-model="citaNueva.hora"-->
<!--                             required="true"-->
<!--                             v-bind:config="configDateStd">-->
<!--                </date-picker>-->
<!--              </div>-->
<!--            </div>-->
<!--          </div>-->
<!--        </template>-->
<!--      </form>-->
    </div>
 </modal-vik>

<script>

module.exports = {
  comoponents:{
    ModalVik: use("_modules/vue-modal")
  },
  data() {
    return {
      visible: false,
      form: "modalNuevoBecados",
      title: "Crear cita tutorizada",
      modalAddBecadosz: VUE_MODAL.structFormAjax({
        id: "modalNuevoBecados",
        okbtn: "Crear cita",
        okclass: "btn-primary",
        modalsize: "modal-lg"
      }),
    };
  },
  methods: {
    abrirModal() {
      this.visible = true;

      this.$refs.modalAddBecadosz.open();
    },
  }
};
</script>