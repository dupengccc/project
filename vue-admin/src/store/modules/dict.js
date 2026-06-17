import { defineStore } from 'pinia'
import { getDicts } from '@/api/dict'

export const useDictStore = defineStore('dict', {
  state: () => ({
    cache: {}
  }),
  actions: {
    async getDict(dictType) {
      if (this.cache[dictType]) {
        return this.cache[dictType]
      }
      try {
        const data = await getDicts(dictType)
        this.cache[dictType] = data || []
        return this.cache[dictType]
      } catch (e) {
        this.cache[dictType] = []
        return []
      }
    },
    getOptions(dictType) {
      const list = this.cache[dictType] || []
      return list.map((it) => ({
        label: it.dictLabel || it.label,
        value: it.dictValue !== undefined ? it.dictValue : it.value
      }))
    },
    getLabel(dictType, value) {
      const list = this.cache[dictType] || []
      const hit = list.find(
        (it) => String(it.dictValue !== undefined ? it.dictValue : it.value) === String(value)
      )
      return hit ? hit.dictLabel || hit.label : ''
    }
  }
})
