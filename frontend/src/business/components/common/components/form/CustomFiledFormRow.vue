<template>
  <div>
    <el-row v-for="(i) in (customFieldRowNums)" :key="i">
    <span class="custom-item" v-for="(item, j) in sortCustomFields" :key="j">
      <span v-if="j >= (i - 1)*3 && j < (i - 1)*3+3">
        <el-col :span="24">
          <el-form-item :label="item.system ? $t(systemNameMap[item.name]) : item.name" :prop="item.name"
                        :label-width="formLabelWidth">
            <custom-filed-component :data="item" :form="form" prop="defaultValue" :disabled="(item.type !== 'richText' && isPublic) || disabled"/>
          </el-form-item>
        </el-col>
      </span>
    </span>
    </el-row>
  </div>
</template>

<script>
import {SYSTEM_FIELD_NAME_MAP} from "@/common/js/table-constants";
import CustomFiledComponent from "@/business/components/project/template/CustomFiledComponent";

export default {
  name: "CustomFiledFormRow",
  components: {CustomFiledComponent},
  props: {
    issueTemplate: {
      type: Object,
      default() {
        return {}
      }
    },
    formLabelWidth: String,
    form: Object,
    isPublic: {
      type: Boolean,
      default() {
        return false;
      }
    },
    disabled: {
      type: Boolean,
      default() {
        return false;
      }
    }
  },
  computed: {
    customFieldRowNums() {
      let size = this.issueTemplate.customFields ? this.issueTemplate.customFields.length : 0
      let val = parseInt(size / 3);
      return size % 3 === 0 ? val : (val + 1);
    },
    systemNameMap() {
      return SYSTEM_FIELD_NAME_MAP;
    },
    sortCustomFields() {
      let total = 0;//定义total用于控制循环结束
      let customFields = this.issueTemplate.customFields;
      for (let i = 0; total < customFields.length; total++) {
        if (typeof (customFields[i].defaultValue) === 'string' || customFields[i].defaultValue instanceof String) {
          try {
            customFields[i].defaultValue = JSON.parse(customFields[i].defaultValue);
          } catch (e) {
            // nothing
          }
        }
        if (customFields[i].type === 'richText') {
          //循环到是0的位置就删除该元素0并且在arr末尾push进这个元素0，由于splice删除了该位置元素，所以i不用+1，下次循环仍然检查i位置的元素
          customFields.push(customFields.splice(i, 1)[0]);
        } else {
          i++;//循环到不是0的位置就继续往后循环
        }
      }
      return customFields;
    },
  }
}
</script>

<style scoped>

</style>
