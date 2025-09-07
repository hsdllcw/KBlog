import { Base } from './base'
export default class Site extends Base {
  id: number | undefined;
  name: string | undefined;
  sign: string | undefined;
  domain: string | undefined;
  templateTheme: string | undefined;
  enabled: boolean | undefined;
  static domain: String = '/site'
  
  constructor() {
    super(Site.domain)
  }

  static getInstance = (object: any) => {
    const site = Object.assign(new Site(), object)
    return site
  }
}
