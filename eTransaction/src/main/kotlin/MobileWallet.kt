open class MobileWallet(nama: String, saldo: Long, noHp: String) : DigitalPayment(nama, saldo) {
    var feeTransferBank: Long = 6000
    val noHp: String = ""


    override fun transfer(dp: DigitalPayment, nominal: Long): Unit {
        if (saldo < 0){
            println("pesan input tidak valid ")
        } else if (saldo <= 50000){
            println("Saldo tidak cukup")
        } else (dp is BRImo || dp is BNImo)
        dp.saldo = saldo - feeTransferBank
    }
}