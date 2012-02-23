package com.sematext.searchschemer.writer;

import java.io.StringWriter;

import org.junit.Test;

import com.sematext.searchschemer.index.BasicIndexStructure;
import com.sematext.searchschemer.index.FieldAttributes;
import com.sematext.searchschemer.index.IndexStructure;
import com.sematext.searchschemer.type.FieldType;

import junit.framework.TestCase;

public class ElasticsearchStructureWriterTest extends TestCase {
  @Test
  public void testWriteNonDynamic() throws Exception {
    IndexStructure structure = new BasicIndexStructure();
    structure.addField("cat", new FieldAttributes(FieldType.STRING, true, true, true), false);

    StringWriter writer = new StringWriter();
    ElasticsearchIndexStructureWriter indexWriter = new ElasticsearchIndexStructureWriter();
    indexWriter.write(structure, writer);

    assertEquals(
        "{\n \"mappings\" : {\n  \"type\" : {\n   \"properties\" : {\n    \"cat\" : { \"type\" : \"string\", \"store\" : \"yes\", \"index\" : \"analyzed\" }\n   }\n  }\n }\n}",
        writer.toString());
    writer.close();
  }

  @Test
  public void testWriteMultipleNonDynamic() throws Exception {
    IndexStructure structure = new BasicIndexStructure();
    structure.addField("cat", new FieldAttributes(FieldType.STRING, true, true, true), false);
    structure.addField("menu", new FieldAttributes(FieldType.STRING, false, true, true), false);

    StringWriter writer = new StringWriter();
    ElasticsearchIndexStructureWriter indexWriter = new ElasticsearchIndexStructureWriter();
    indexWriter.write(structure, writer);

    assertEquals(
        "{\n \"mappings\" : {\n  \"type\" : {\n   \"properties\" : {\n    \"cat\" : { \"type\" : \"string\", \"store\" : \"yes\", \"index\" : \"analyzed\" },\n    \"menu\" : { \"type\" : \"string\", \"store\" : \"yes\", \"index\" : \"no\" }\n   }\n  }\n }\n}",
        writer.toString());
    writer.close();
  }

  @Test
  public void testWriteDynamic() throws Exception {
    IndexStructure structure = new BasicIndexStructure();
    structure.addField("*_string", new FieldAttributes(FieldType.STRING, false, true, true), true);

    StringWriter writer = new StringWriter();
    ElasticsearchIndexStructureWriter indexWriter = new ElasticsearchIndexStructureWriter();
    indexWriter.write(structure, writer);

    assertEquals(
        "{\n \"mappings\" : {\n  \"type\" : {\n   \"dynamic_templates\" : [\n    {\n     \"template__string\" : {\n      \"match\" : \"*_string\",\n      \"match_mapping_type\" : \"string\",\n      \"mapping\" : { \"type\" : \"string\", \"store\" : \"yes\", \"index\" : \"no\" }\n     }\n    }\n   ]\n  }\n }\n}",
        writer.toString());
    writer.close();
  }
}