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
          id: 1, name: '集团总部', code: 'G001', type: 'group', sort: 1, status: 0,
          createTime: '2023-01-01 09:00:00', creator: '系统管理员',
          children: [
            {
              id: 2, name: '华东分公司', code: 'B001', type: 'branch', sort: 1, status: 0,
              createTime: '2023-01-02 10:00:00', creator: '张三',
              children: [
                {
                  id: 5, name: '研发部', code: 'D001', type: 'dept', sort: 1, status: 0,
                  createTime: '2023-01-03 11:00:00', creator: '李四',
                  children: [
                    { id: 8, name: '前端组', code: 'T001', type: 'dept', sort: 1, status: 0, createTime: '2023-01-04 08:00:00', creator: '李四', children: [] },
                    { id: 9, name: '后端组', code: 'T002', type: 'dept', sort: 2, status: 0, createTime: '2023-01-04 08:30:00', creator: '李四', children: [] }
                  ]
                },
                { id: 6, name: '市场部', code: 'D002', type: 'dept', sort: 2, status: 0, createTime: '2023-01-05 09:00:00', creator: '王五', children: [] }
              ]
            },
            {
              id: 3, name: '华南分公司', code: 'B002', type: 'branch', sort: 2, status: 0,
              createTime: '2023-01-06 10:00:00', creator: '张三',
              children: [
                { id: 7, name: '运营部', code: 'D003', type: 'dept', sort: 1, status: 0, createTime: '2023-01-07 09:00:00', creator: '赵六', children: [] }
              ]
            },
            { id: 4, name: '财务部', code: 'D004', type: 'dept', sort: 3, status: 1, createTime: '2023-01-08 10:00:00', creator: '系统管理员', children: [] }
          ]
        }
      ]
    }
  }
})
