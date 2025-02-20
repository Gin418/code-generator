import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import '@umijs/max';
import React from 'react';

const Footer: React.FC = () => {
  const defaultMessage = '程序员Gin';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          title: '浙ICP备2025145726号-1',
          href: 'https://beian.miit.gov.cn/#/Integrated/index',
          blankTarget: true,
        },
        {
          key: 'github',
          title: (
            <>
              <GithubOutlined /> 源码
            </>
          ),
          href: 'https://github.com/Gin418',
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
