import Mock from 'mockjs'

const count = 30
const getPageList = {}
const pageList = []
for(let i = 0; i < count; i++) {
  pageList.push(Mock.mock({
    id: '@id()',
    title: '@ctitle()',
    author: '@cname()',
    date: '@datetime()',
    status: '@character("ABC")'
  }))
}

getPageList['message'] = 'success'
getPageList['statusCode'] = '200'
getPageList['result'] = pageList

export { getPageList }
