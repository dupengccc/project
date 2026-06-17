import { defineStore } from 'pinia'
import { getOrgTree } from '@/api/org'

export const useOrgStore = defineStore('org', {
  state: () => ({
    tree: [],
    flatList: []
  }),
  actions: {
    async loadTree() {
      try {
        const data = await getOrgTree()
        this.tree = data || []
        this.flatList = this.flatten(this.tree)
        return this.tree
      } catch (e) {
        // 后端未就绪，使用模拟数据
        this.tree = this.getMockTree()
        this.flatList = this.flatten(this.tree)
        return this.tree
      }
    },
    flatten(nodes, result = []) {
      for (const node of nodes) {
        result.push({ value: node.id, label: node.name, parentId: node.parentId })
        if (node.children?.length) {
          this.flatten(node.children, result)
        }
      }
      return result
    },
    getMockTree() {
      return [
        {
          id: 1,
          name: '集团总部',
          type: 'group',
          sort: 1,
          status: 0,
          children: [
            {
              id: 2,
              name: '华东分公司',
              type: 'branch',
              sort: 1,
              status: 0,
              children: [
                {
                  id: 5,
                  name: '研发部',
                  type: 'dept',
                  sort: 1,
                  status: 0,
                  children: [
                    { id: 8, name: '前端组', type: 'dept', sort: 1, status: 0, children: [] },
                    { id: 9, name: '后端组', type: 'dept', sort: 2, status: 0, children: [] }
                  ]
                },
                { id: 6, name: '市场部', type: 'dept', sort: 2, status: 0, children: [] }
              ]
            },
            {
              id: 3,
              name: '华南分公司',
              type: 'branch',
              sort: 2,
              status: 0,
              children: [
                {
                  id: 7,
                  name: '运营部',
                  type: 'dept',
                  sort: 1,
                  status: 0,
                  children: []
                }
              ]
            },
            {
              id: 4,
              name: '财务部',
              type: 'dept',
              sort: 3,
              status: 1,
              children: []
            }
          ]
        }
      ]
    }
  }
})
