package com.edward.contract;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/*
* MXC代币测试用例
*
* @Auther chenxue
*
* Aug. 31th 2018
*
* 跑完测试用例后访问：https://rinkeby.etherscan.io/token/0x29af5927ee243c31cbbdcf5b95b3ffbdb471af4d?a=0x1bd6f325d8186d21fc96c42412f454283134d985查询结果
*
* **/
public class MXCTest {

   /**
    * 合约地址，我以rinkeby测试网络下MXC币为例
    * */
   private static String contractAddress = "0x29af5927ee243c31cbbdcf5b95b3ffbdb471af4d";

   /**
    * 以太坊节点地址，我本地没有搭建以太坊节点，直接使用infura提供的节点
    * */
   //private static String nodeHttpAddress = "https://rinkeby.infura.io/aI4tThsezSNVlRMF12S4";
   private static String nodeHttpAddress = "http://127.0.0.1:8545";

   /**
    * 用户私钥信息
    * 为了简化处理，此处采用直接导入私钥的方式生成credentials，在生产环境一般会采用keystore + 密码方式
    *
    * */
   private static Credentials credentials = Credentials.create("8cac5ccd9f0ff2eb050c9b30b7d9ed455183bb4ebbccab34b651fb111e5d1555");

   /**
    * Web3j 客户端，启动测试用例时初始化
    * */
   private static Web3j web3j = null;

   /**
    * MXC代币客户端实例
    * */
   private static MXC contract = null;

   @BeforeClass
   public static void setting(){
      web3j = Web3j.build(new HttpService(nodeHttpAddress));
      contract = MXC.load(contractAddress, web3j, credentials, new BigInteger(Convert.toWei("1",Convert.Unit.GWEI).toString()), org.web3j.tx.Contract.GAS_LIMIT);
   }


   /**
    * 余额查询测试用例
    *
    * **/
   @Test
   public void testBalanceOf() throws Exception {
      try{
         BigInteger result = contract.balanceOf("0x1bd6f325d8186d21fc96c42412f454283134d985").send();
         System.out.println("balanceOf：" + result);
         Assert.assertNotNull(result);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }

   }

   /*
   * 单笔转账测试用例
   *
   * **/
   @Test
   public void testTransfer() throws Exception{
      try{
         TransactionReceipt result = contract.transfer("0xe3220b7B042BB12F3B2E0592DB4A04676aCaC465",new BigInteger(Convert.toWei("181",Convert.Unit.ETHER).toString())).send();
         System.out.println("testTransfer result：" + bean2String(result));
         Assert.assertNotNull(result);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   /**
    * 批量转账测试用例，每个地址转账数量一致
    *
    * **/
   @Test
   public void testBatchTransfer() throws Exception {
      try{
         //批量转账
         List receivers = new ArrayList<String>();
         receivers.add("0xe3220b7B042BB12F3B2E0592DB4A04676aCaC465");
         receivers.add("0xB6d2B76D8bD7Ea3a3630BD9f126112cb9769ce8B");
         BigInteger value = new BigInteger(Convert.toWei("182",Convert.Unit.ETHER).toString());
         RemoteCall<TransactionReceipt> remoteCall = contract.batchTransfer(receivers,value);
         TransactionReceipt result = remoteCall.send();
         System.out.println("testBatchTransfer result：" + bean2String(result));
         Assert.assertNotNull(result);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }

   }

   /**
    * 批量转账测试用例，每个地址转账金额不一致　
    *
    * **/
   @Test
   public void testBatchTransfers() throws Exception {
      try{
         //批量转账
         List receivers = new ArrayList<String>();
         receivers.add("0xe3220b7B042BB12F3B2E0592DB4A04676aCaC465");
         receivers.add("0xB6d2B76D8bD7Ea3a3630BD9f126112cb9769ce8B");
         BigInteger value = new BigInteger(Convert.toWei("182",Convert.Unit.ETHER).toString());
         RemoteCall<TransactionReceipt> remoteCall = contract.batchTransfer(receivers,value);
         TransactionReceipt result = remoteCall.send();
         System.out.println("testBatchTransfers result：" + bean2String(result));
         Assert.assertNotNull(result);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   /**
    * 查询以太坊余额　
    *
    * **/
   @Test
   public void testEthGetBalance() throws Exception {
      try{

         EthGetBalance ethGetBalance = web3j.ethGetBalance("0x48db3ed9e0d28b0e6d9f5507820b66f7404ab183", DefaultBlockParameter.valueOf("latest")).send();
         System.out.println("testEthGetBalance result：" + ethGetBalance.getBalance());
         Assert.assertNotNull(ethGetBalance);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   /**
    * 查询以太坊余额　
    *
    * **/
   @Test
   public void testEthGetBlock() throws Exception {
      try{

         EthBlock ethGetBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger("2936568")), false).send();
         System.out.println("testEthGetBalance result：" + ethGetBlock.getBlock().getGasUsed());
         Assert.assertNotNull(ethGetBlock);
      } catch (Exception e) {
         e.printStackTrace();
         throw e;
      }
   }

   /*
   * 将对象转换为Json字符串，方便打印日志
   * **/
   private static String bean2String(Object object) {
      ObjectMapper mapper = new ObjectMapper();
      try{
         return mapper.writeValueAsString(object);
      }catch(Exception e){
         e.printStackTrace();
         return "transfer bean to string err";
      }

   }
}
