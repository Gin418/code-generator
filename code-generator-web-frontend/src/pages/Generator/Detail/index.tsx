import AuthorInfo from '@/pages/Generator/Detail/components/AuthorInfo';
import FileConfig from '@/pages/Generator/Detail/components/FileConfig';
import ModelConfig from '@/pages/Generator/Detail/components/ModelConfig';
import {
  deleteGeneratorUsingPost,
  downloadGeneratorByIdUsingGet,
  getGeneratorVoByIdUsingGet,
} from '@/services/backend/generatorController';
import {Link, useParams} from '@@/exports';
import {DeleteOutlined, DownloadOutlined, EditOutlined} from '@ant-design/icons';
import {PageContainer} from '@ant-design/pro-components';
import {history, useModel} from '@umijs/max';
import {Button, Card, Col, Image, message, Row, Space, Tabs, Tag, Typography} from 'antd';
import {saveAs} from 'file-saver';
import moment from 'moment';
import React, {useEffect, useState} from 'react';

/**
 * 生成器详情页面
 * @constructor
 */

const GeneratorDetailPage: React.FC = () => {
  const { id } = useParams();

  const [loading, setLoading] = useState<boolean>(false);
  const [data, setData] = useState<API.GeneratorVO>({});
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const my = currentUser?.id === data?.userId;

  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({
        // @ts-ignore
        id,
      });
      setData(res.data || {});
    } catch (e: any) {
      message.error('获取数据失败,' + e.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const tagListView = (tags: string[] | undefined) => {
    if (!tags) {
      return <></>;
    }
    return (
      <div style={{ marginBottom: 8 }}>
        {tags.map((tag: string) => {
          return <Tag key={tag}>{tag}</Tag>;
        })}
      </div>
    );
  };

  /**
   * 立即使用
   */
  const useButton = (
    <Button
      type="primary"
      onClick={() => {
        console.log(currentUser);
        if (!currentUser) {
          message.error("请先登录")
          return;
        }
        if (!data.distPath) {
          message.error("请先上传生成器文件")
        } else {
          history.push(`/generator/use/${id}`);
        }
      }}
    >
      立即使用
    </Button>
  )

  /**
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      icon={<DownloadOutlined />}
      onClick={async () => {
        const blob = await downloadGeneratorByIdUsingGet(
          {
            id: data.id,
          },
          {
            responseType: 'blob',
          },
        );
        // 使用 file-saver 来保存文件
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
      }}
    >
      下载
    </Button>
  );

  /**
   * 编辑
   */
  const editButton = my && (
    <Link to={`/generator/update?id=${data.id}`}>
      <Button icon={<EditOutlined />}>编辑</Button>
    </Link>
  );

  /**
   * 删除
   */
  const deleteButton = my && (
    <Button
      icon={<DeleteOutlined />}
      onClick={async () => {
        const res = await deleteGeneratorUsingPost(
          {
            id: data.id,
          },
        );

        if (res.data) {
          message.success("删除成功");
          history.push(`/`);
        } else {
          message.error("删除失败");
        }
      }}
    >
      删除
    </Button>
  );

  return (
    <PageContainer loading={loading}>
      <Card>
        <Row justify="space-between" gutter={[16, 16]}>
          <Col flex="60%">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Typography.Paragraph>{data.description}</Typography.Paragraph>
            <Typography.Paragraph type={'secondary'}>
              创建时间：{moment(data.createTime).format('YYYY-MM-DD HH:mm:ss')}
            </Typography.Paragraph>
            <Typography.Paragraph type="secondary">基础包：{data.basePackage}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">版本：{data.version}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">作者：{data.author}</Typography.Paragraph>
            <div style={{ marginBottom: 24 }} />
            <Space size="middle">
              {useButton}
              {downloadButton}
              {editButton}
              {deleteButton}
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </Card>
      <div style={{ marginBottom: 24 }} />
      <Card>
        <Tabs
          size="large"
          defaultActiveKey="fileConfig"
          onChange={() => {}}
          items={[
            {
              key: 'fileConfig',
              label: '文件配置',
              children: <FileConfig data={data} />,
            },
            {
              key: 'modelConfig',
              label: '模型配置',
              children: <ModelConfig data={data} />,
            },
            {
              key: 'userInfo',
              label: '作者信息',
              children: <AuthorInfo data={data} />,
            },
          ]}
        />
      </Card>
    </PageContainer>
  );
};

export default GeneratorDetailPage;
