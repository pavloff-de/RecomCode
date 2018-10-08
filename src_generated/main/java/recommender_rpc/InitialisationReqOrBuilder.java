// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: recom_rpc.proto

package recommender_rpc;

public interface InitialisationReqOrBuilder extends
    // @@protoc_insertion_point(interface_extends:recommender_rpc.InitialisationReq)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * Path to a file with settings of the recommender
   * </pre>
   *
   * <code>string settings_path = 1;</code>
   */
  java.lang.String getSettingsPath();
  /**
   * <pre>
   * Path to a file with settings of the recommender
   * </pre>
   *
   * <code>string settings_path = 1;</code>
   */
  com.google.protobuf.ByteString
      getSettingsPathBytes();

  /**
   * <pre>
   * Targeted prg language (python, java, ..)
   * </pre>
   *
   * <code>string prog_language = 2;</code>
   */
  java.lang.String getProgLanguage();
  /**
   * <pre>
   * Targeted prg language (python, java, ..)
   * </pre>
   *
   * <code>string prog_language = 2;</code>
   */
  com.google.protobuf.ByteString
      getProgLanguageBytes();

  /**
   * <pre>
   * Path to the edited source file
   * </pre>
   *
   * <code>string src_file_path = 3;</code>
   */
  java.lang.String getSrcFilePath();
  /**
   * <pre>
   * Path to the edited source file
   * </pre>
   *
   * <code>string src_file_path = 3;</code>
   */
  com.google.protobuf.ByteString
      getSrcFilePathBytes();

  /**
   * <pre>
   * Path to all sources
   * </pre>
   *
   * <code>string src_dir_path = 4;</code>
   */
  java.lang.String getSrcDirPath();
  /**
   * <pre>
   * Path to all sources
   * </pre>
   *
   * <code>string src_dir_path = 4;</code>
   */
  com.google.protobuf.ByteString
      getSrcDirPathBytes();

  /**
   * <pre>
   * Optional labels for domains of the developed code (e.g. pandas, numpy, ..)
   * </pre>
   *
   * <code>repeated string domain_labels = 5;</code>
   */
  java.util.List<java.lang.String>
      getDomainLabelsList();
  /**
   * <pre>
   * Optional labels for domains of the developed code (e.g. pandas, numpy, ..)
   * </pre>
   *
   * <code>repeated string domain_labels = 5;</code>
   */
  int getDomainLabelsCount();
  /**
   * <pre>
   * Optional labels for domains of the developed code (e.g. pandas, numpy, ..)
   * </pre>
   *
   * <code>repeated string domain_labels = 5;</code>
   */
  java.lang.String getDomainLabels(int index);
  /**
   * <pre>
   * Optional labels for domains of the developed code (e.g. pandas, numpy, ..)
   * </pre>
   *
   * <code>repeated string domain_labels = 5;</code>
   */
  com.google.protobuf.ByteString
      getDomainLabelsBytes(int index);
}
