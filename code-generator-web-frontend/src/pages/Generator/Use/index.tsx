import {getGeneratorVoByIdUsingGet, useGeneratorUsingPost,} from '@/services/backend/generatorController';
import {Link, useParams} from '@@/exports';
import {DownloadOutlined} from '@ant-design/icons';
import {PageContainer} from '@ant-design/pro-components';
import {useModel} from '@umijs/max';
import {Button, Card, Col, Collapse, Form, Image, Input, message, Radio, Row, Space, Tag, Typography,} from 'antd';
import {saveAs} from 'file-saver';
import React, {useEffect, useState} from 'react';

/**
 * 生成器使用页面
 * @constructor
 */

const GeneratorUsePage: React.FC = () => {
  const { id } = useParams();

  const [loading, setLoading] = useState<boolean>(false);
  const [downloading, setDownloading] = useState<boolean>(false);
  const [data, setData] = useState<API.GeneratorVO>({});
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const [form] = Form.useForm();

  const models = data?.modelConfig?.models ?? [];

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

  /**
   * 标签列表视图
   * @param tags
   */
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
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      loading={downloading}
      type="primary"
      icon={<DownloadOutlined />}
      onClick={async () => {
        setDownloading(true);
        let value = form.getFieldsValue();
        console.log(value);
        // eslint-disable-next-line react-hooks/rules-of-hooks
        const blob = await useGeneratorUsingPost(
          {
            id: data.id,
            dataModel: value,
          },
          {
            responseType: 'blob',
          },
        );
        // 使用 file-saver 来保存文件
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
        setDownloading(false);
      }}
    >
      生成代码
    </Button>
  );

  // @ts-ignore
  const renderFormItem = (model) => {
    if (model.type === 'boolean') {
      return (
        <Radio.Group>
          <Radio value={true}>是</Radio>
          <Radio value={false}>否</Radio>
        </Radio.Group>
      );
    }

    return <Input placeholder={model.description} />;
  };

  return (
    <PageContainer title={<></>} loading={loading}>
      <Card>
        <Row justify="space-between" gutter={[32, 32]}>
          <Col flex="60%">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Typography.Paragraph>{data.description}</Typography.Paragraph>
            <Form form={form}>
              {models.map((model, index) => {
                // 是分组
                if (model.groupKey) {
                  if (!model.models) {
                    return <></>;
                  }

                  return (
                    <Collapse
                      key={index}
                      style={{
                        marginBottom: 24,
                      }}
                      items={[
                        {
                          key: index,
                          label: model.groupName + ' (分组) ',
                          children: model.models.map((submodel, index) => {
                            return (
                              <Form.Item
                                key={index}
                                label={submodel.fieldName}
                                // @ts-ignore
                                name={[model.groupKey, submodel.fieldName]}
                                initialValue={submodel.defaultValue}
                              >
                                {renderFormItem(submodel)}
                              </Form.Item>
                            );
                          }),
                        },
                      ]}
                      bordered={false}
                      defaultActiveKey={[index]}
                    />
                  );
                }

                return (
                  <Form.Item
                    key={index}
                    label={model.fieldName}
                    name={model.fieldName}
                    initialValue={model.defaultValue}
                  >
                    {renderFormItem(model)}
                  </Form.Item>
                );
              })}
            </Form>

            <Space size="middle">
              {downloadButton}
              <Link to={`/generator/detail/${id}`}>
                <Button>查看详情</Button>
              </Link>
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </Card>
    </PageContainer>
  );
};

export default GeneratorUsePage;
