// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: recom_rpc.proto

package recommender_rpc;

public interface SuggestionsSetRespOrBuilder extends
    // @@protoc_insertion_point(interface_extends:recommender_rpc.SuggestionsSetResp)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 sessionId = 1;</code>
   */
  long getSessionId();

  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  java.util.List<recommender_rpc.SuggesionData> 
      getSuggestionsList();
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  recommender_rpc.SuggesionData getSuggestions(int index);
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  int getSuggestionsCount();
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  java.util.List<? extends recommender_rpc.SuggesionDataOrBuilder> 
      getSuggestionsOrBuilderList();
  /**
   * <code>repeated .recommender_rpc.SuggesionData suggestions = 2;</code>
   */
  recommender_rpc.SuggesionDataOrBuilder getSuggestionsOrBuilder(
      int index);
}
