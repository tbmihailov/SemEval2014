#!/usr/bin/perl
#
#  Author: Todor Mihaylov
#  
#  Description: Read feature prefix filters
#
#  Last modified: May 14, 2014
#
#

use warnings;
use strict;
use utf8;


my $predictions_file = $ARGV[0] or die "No predictions file is specified\n";


print STDERR "Reading predictions file $predictions_file \n";
### 0. Build a list of predictions
open PREDICTION, $predictions_file or die;
#binmode (PREDICTION,  ":utf8");
#write to prediction tests
open OUTPUT, ">$predictions_file\.text" or die;
#	binmode (OUTPUT,  ":utf8");

my $index = 1;
while(<PREDICTION>) {
	die "Wrong feature: 'line' - not a 1,2,3" if (!/^([1-3]+).+$/);
	print OUTPUT "NA\t";
	if (defined $1){
		print OUTPUT "$index\t";
		if($1 =~/1/){
			print OUTPUT "positive";
		}elsif($1 =~/2/){
			print OUTPUT "negative";
		}elsif($1 =~/3/){
			print OUTPUT "neutral";
		}else {
			print OUTPUT "unknown";
		}
	}
	print OUTPUT "\n";
	$index++;
}
close PREDICTION or die;
close OUTPUT or die;
