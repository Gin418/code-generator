import {listGeneratorVoByPageFastUsingPost} from '@/services/backend/generatorController';
import {UserOutlined} from '@ant-design/icons';
import {PageContainer, ProFormSelect, ProFormText} from '@ant-design/pro-components';
import {QueryFilter} from '@ant-design/pro-form/lib';
import {Avatar, Card, Flex, Image, Input, List, message, Tabs, Tag, Typography} from 'antd';
import moment from 'moment';
import React, {useEffect, useState} from 'react';
import {Link} from '@@/exports';

/**
 * 默认分页参数
 */
const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 8,
  sortField: 'createTime',
  sortOrder: 'descend',
};

/**
 * 主页
 * @constructor
 */
const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);
  const [total, setTotal] = useState<number>(0);
  // 搜索条件
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS,
  });

  /**
   * 搜索
   */
  const doSearch = async () => {
    setLoading(true);
    try {
      const res = await listGeneratorVoByPageFastUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    } catch (error: any) {
      message.error('获取数据失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    doSearch();
  }, [searchParams]);

  /**
   * 标签列表
   * @param tags
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  return (
    <PageContainer title={<></>}>
      <Flex justify={'center'}>
        <Input.Search
          style={{
            width: '40vw',
            minWidth: 320,
          }}
          placeholder="请搜索生成器"
          allowClear
          enterButton="搜索"
          size="large"
          onChange={(e) => {
            searchParams.searchText = e.target.value;
          }}
          onSearch={(value) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText: value,
              sortField: searchParams.sortField,
              sortOrder: searchParams.sortOrder,
            });
          }}
        />
      </Flex>
      <div style={{ marginBottom: 16 }}></div>
      <Tabs
        size={'large'}
        defaultActiveKey="1"
        items={[
          {
            key: 'newest',
            label: '最新',
          },
          {
            key: 'recommend',
            label: '推荐',
          },
        ]}
        onChange={(activeKey: string) => {
          console.log(activeKey);
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            searchText: searchParams.searchText,
            sortField: activeKey === 'newest' ? 'createTime' : 'useNum',
          })
        }}
      />
      <QueryFilter
        // submitter={false}
        defaultCollapsed={true}
        span={8}
        labelWidth="auto"
        labelAlign="left"
        style={{ padding: '16px 0' }}
        split
        onFinish={async (values: API.GeneratorQueryRequest) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            searchText: searchParams.searchText,
            sortField: searchParams.sortField,
            sortOrder: searchParams.sortOrder,
            ...values,
          });
        }}
      >
        <ProFormText name="name" label="名称" />
        <ProFormText name="description" label="描述" />
        <ProFormSelect name="tags" label="标签" mode="tags" />
      </QueryFilter>
      <div style={{ marginBottom: 24 }} />
      <List<API.GeneratorVO>
        rowKey="id"
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        loading={loading}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange(current, pageSize) {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item key={data.id}>
            <Card hoverable cover={<Image alt={data.name} src={data.picture} />}>
              <Link to={`/generator/detail/${data.id}`}>
                <Card.Meta
                  title={<a>{data.name}</a>}
                  description={
                    <Typography.Paragraph ellipsis={{ rows: 2 }} style={{ height: 44 }}>
                      {data.description}
                    </Typography.Paragraph>
                  }
                />
                {tagListView(data.tags)}

                <Flex justify={'space-between'} align={'center'}>
                  <Typography.Paragraph type="secondary" style={{ fontSize: 12 }}>
                    {moment(data.createTime).fromNow()}
                  </Typography.Paragraph>
                  <div>
                    <Avatar src={data.user?.userAvatar ?? <UserOutlined />} />
                  </div>
                </Flex>
              </Link>
            </Card>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};

export default IndexPage;
